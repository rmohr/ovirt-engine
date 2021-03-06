package org.ovirt.engine.ui.common.restapi;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ovirt.engine.ui.common.utils.HttpUtils;
import org.ovirt.engine.ui.frontend.Frontend;
import org.ovirt.engine.ui.frontend.communication.EngineSessionRefreshedEvent;
import org.ovirt.engine.ui.frontend.communication.EngineSessionRefreshedEvent.EngineSessionRefreshedHandler;
import org.ovirt.engine.ui.frontend.communication.StorageCallback;
import org.ovirt.engine.ui.frontend.utils.BaseContextPathData;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.inject.Inject;

/**
 * Takes care of Engine REST API session management for UI plugin infrastructure.
 * <p>
 * This class has following responsibilities:
 * <ul>
 * <li>acquire new session upon successful user authentication (classic login)
 * <li>reuse existing session if the user is already authenticated (auto login)
 * <li>keep the current session alive while the user stays authenticated
 * </ul>
 * <p>
 * <b>
 * Important: acquired (physical) REST API session maps to current user's (logical) Engine session.
 * </b>
 * <p>
 * This means that the REST API session is usable only while the corresponding Engine session
 * is alive. Once the user logs out, corresponding Engine session will expire and any unclosed
 * physical sessions that map to it will become unusable.
 * <p>
 * Triggers {@link RestApiSessionAcquiredEvent} upon acquiring or reusing REST API session.
 */
public class RestApiSessionManager implements EngineSessionRefreshedHandler {

    private static class RestApiRequestCallback implements RequestCallback {

        @Override
        public void onResponseReceived(Request request, Response response) {
            if (response.getStatusCode() == Response.SC_OK) {
                processResponse(response);
            } else {
                RestApiSessionManager.logger.warning(
                        "Engine REST API responded with non-OK status code " //$NON-NLS-1$
                                + response.getStatusCode());
            }
        }

        @Override
        public void onError(Request request, Throwable exception) {
            RestApiSessionManager.logger.log(Level.WARNING,
                    "Error while dispatching Engine REST API request", exception); //$NON-NLS-1$
        }

        protected void processResponse(Response response) {
            // No-op, override as necessary
        }

    }

    private static final Logger logger = Logger.getLogger(RestApiSessionManager.class.getName());

    /**
     * The name of the header used to explicitly request a specific version of the API.
     */
    private static final String VERSION_HEADER = "Version"; //$NON-NLS-1$

    /**
     * The value of the header used to explicitly request a specific version of the API.
     */
    private static final String VERSION_VALUE = "4"; //$NON-NLS-1$

    private static final String PREFER_HEADER = "Prefer"; //$NON-NLS-1$
    private static final String FILTER_HEADER = "Filter"; //$NON-NLS-1$
    private static final String SESSION_ID_HEADER = "JSESSIONID"; //$NON-NLS-1$
    private static final String CSRF_HEADER = "JSESSIONID"; //$NON-NLS-1$
    private static final String HEADER_AUTHORIZATION = "Authorization"; //$NON-NLS-1$

    private static final String SESSION_ID_KEY = "RestApiSessionId"; //$NON-NLS-1$
    private static final int DEFAULT_ENGINE_SESSION_TIMEOUT = 30;
    private static final int DEFAULT_HARD_LIMIT = 600;

    private static final int MIN_IN_MS = 1000 * 60;

    // Heartbeat (delay) between REST API keep-alive requests
    private static final int SESSION_HEARTBEAT_MS = MIN_IN_MS;

    private final EventBus eventBus;
    private final String restApiBaseUrl;

    private int restApiSessionTimeout;

    private Integer restApiSessionHardlimit;
    //On logout the page reloads and this will be reset.
    private Date restApiLoginTimePlusHardLimit;

    private String restApiSessionId;

    private boolean refreshRestApiSession = false;

    @Inject
    public RestApiSessionManager(EventBus eventBus) {
        this.eventBus = eventBus;

        // Note that the slash at the end of the URL is not just a whim. With the trailing slash the browser will only
        // send authentication headers to URLs ending in api/, otherwise it will send them to URLs ending in /, and
        // this causes problems in other applications, for example in the reports application.
        this.restApiBaseUrl = BaseContextPathData.getPath() + "api/"; //$NON-NLS-1$

        setSessionTimeout(DEFAULT_ENGINE_SESSION_TIMEOUT);
        eventBus.addHandler(EngineSessionRefreshedEvent.getType(), this);
    }

    @Override
    public void onEngineSessionRefreshed(EngineSessionRefreshedEvent event) {
        if (restApiSessionId != null && (restApiLoginTimePlusHardLimit == null
                || new Date().before(restApiLoginTimePlusHardLimit))) {
            refreshRestApiSession = true;
        }
    }

    public void setSessionTimeout(String engineSessionTimeout) {
        try {
            setSessionTimeout(Integer.parseInt(engineSessionTimeout));
        } catch (NumberFormatException ex) {
            setSessionTimeout(DEFAULT_ENGINE_SESSION_TIMEOUT);
        }
    }

    public void setSessionTimeout(int engineSessionTimeout) {
        // Engine session expiration happens through periodic "cleanExpiredUsersSessions" job
        // whose periodicity is same as Engine session timeout (UserSessionTimeOutInterval).
        // Because of that, Engine sessions can stay active up to 2 * UserSessionTimeOutInterval
        // so we adapt REST API session timeout accordingly.
        restApiSessionTimeout = 2 * engineSessionTimeout;
    }

    public void setHardLimit(String sessionHardLimit) {
        try {
            restApiSessionHardlimit = Integer.valueOf(sessionHardLimit); //Minutes
        } catch (NumberFormatException ex) {
            restApiSessionHardlimit = DEFAULT_HARD_LIMIT;
        }
    }

    public void recordLoggedInTime() {
        if (restApiSessionHardlimit > 0) {
            restApiLoginTimePlusHardLimit = new Date();
            restApiLoginTimePlusHardLimit.setTime(restApiLoginTimePlusHardLimit.getTime()
                    + ((restApiSessionHardlimit.longValue() - 1) * MIN_IN_MS)); //Subtract one refresh cycle to be sure we stop.
        }
    }

    /**
     * Build HTTP request to keep-alive existing REST API session.
     */
    RequestBuilder createRequest() {
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, restApiBaseUrl);

        // Indicate explicitly the requested version of the API:
        builder.setHeader(VERSION_HEADER, VERSION_VALUE);

        // Control REST API session timeout
        builder.setHeader("Session-TTL", String.valueOf(restApiSessionTimeout)); //$NON-NLS-1$

        // Express additional preferences for serving this request
        builder.setHeader(PREFER_HEADER, "persistent-auth, csrf-protection"); //$NON-NLS-1$

        boolean isAdmin = Frontend.getInstance().getLoggedInUser().isAdmin();
        builder.setHeader(FILTER_HEADER, String.valueOf(!isAdmin));

        // Add CSRF token, this is needed due to Prefer:csrf-protection
        if (restApiSessionId != null) {
            builder.setHeader(CSRF_HEADER, restApiSessionId);
        }

        return builder;
    }

    /**
     * Build HTTP request to acquire new REST API session.
     */
    RequestBuilder createRequest(String engineAuthToken) {
        RequestBuilder builder = createRequest();

        // Enforce expiry of existing session when acquiring new session
        String preferValue = builder.getHeader(PREFER_HEADER);
        builder.setHeader(PREFER_HEADER, preferValue + ", new-auth"); //$NON-NLS-1$

        // Map this (physical) REST API session to current user's (logical) Engine session
        builder.setHeader(HEADER_AUTHORIZATION, "Bearer " + engineAuthToken); //$NON-NLS-1$
        return builder;
    }

    void sendRequest(RequestBuilder requestBuilder, RestApiRequestCallback callback) {
        try {
            requestBuilder.sendRequest(null, callback);
        } catch (RequestException e) {
            // Request failed to initiate, nothing we can do about it
        }
    }

    void scheduleKeepAliveHeartbeat() {
        Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
            @Override
            public boolean execute() {
                boolean sessionInUse = restApiSessionId != null;

                if (sessionInUse && refreshRestApiSession) {
                    // The browser takes care of sending JSESSIONID cookie for this request automatically
                    sendRequest(createRequest(), new RestApiRequestCallback());

                    // Reset the refresh flag
                    refreshRestApiSession = false;
                }

                // Proceed with the heartbeat only when the session is still in use
                return sessionInUse;
            }
        }, SESSION_HEARTBEAT_MS);
    }

    /**
     * Acquires new REST API session that maps to current user's Engine session.
     */
    public void acquireSession(String engineAuthToken) {
        sendRequest(createRequest(engineAuthToken), new RestApiRequestCallback() {
            @Override
            protected void processResponse(Response response) {
                // Obtain session ID from response header, as we're unable to access the
                // JSESSIONID cookie directly (cookie is set for REST API specific path)
                String sessionIdFromHeader = HttpUtils.getHeader(response, SESSION_ID_HEADER);
                if (sessionIdFromHeader != null) {
                    setSessionId(sessionIdFromHeader, true);
                }

                reuseSession();
            }
        });
    }

    /**
     * Attempts to reuse existing REST API session that was previously acquired.
     */
    public void reuseSession() {
        // If reuseSession is called right after setSessionId, then getSessionId() without the callback will not
        // be null. If it is null then reuseSession was called from an automatic login (as restApiSessionId is null
        // can we can utilize the async call to retrieve it from the backend.
        if (restApiSessionId != null) {
            processSessionId(restApiSessionId);
        } else {
            getSessionIdFromHttpSession(new StorageCallback() {
                @Override
                public void onSuccess(String result) {
                    if (result != null) {
                        setSessionId(result, false);
                        processSessionId(result);
                    } else {
                        processSessionIdException();
                    }
                }

                @Override
                public void onFailure(Throwable caught) {
                    processSessionIdException();
                }
            });
        }
    }

    void processSessionId(String sessionId) {
        RestApiSessionAcquiredEvent.fire(eventBus, sessionId);
        scheduleKeepAliveHeartbeat();
    }

    void processSessionIdException() {
        logger.severe("Engine REST API session ID is not available"); //$NON-NLS-1$
    }

    /**
     * Releases existing REST API session.
     * <p>
     * Note that we're not closing (physical) REST API session via HTTP request since the user
     * logout operation already triggered (logical) Engine session expiry. Even if the physical
     * session is still alive (JSESSIONID cookie still valid), it won't work when the associated
     * logical session is dead.
     */
    public void releaseSession() {
        setSessionId(null, true);
    }

    void getSessionIdFromHttpSession(StorageCallback callback) {
        Frontend.getInstance().retrieveFromHttpSession(SESSION_ID_KEY, callback);
    }

    void setSessionId(String sessionId, boolean storeInHttpSession) {
        if (storeInHttpSession) {
            Frontend.getInstance().storeInHttpSession(SESSION_ID_KEY, sessionId);
        }
        this.restApiSessionId = sessionId;
    }

}
