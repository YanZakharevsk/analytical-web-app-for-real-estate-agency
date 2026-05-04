const SESSION_KEY = 'rea_analytics_session';

function newSessionId() {
    if (typeof crypto !== 'undefined' && crypto.randomUUID) {
        return crypto.randomUUID();
    }
    return `${Date.now()}-${Math.random().toString(16).slice(2)}`;
}

export function getAnalyticsSessionId() {
    try {
        let id = sessionStorage.getItem(SESSION_KEY);
        if (!id) {
            id = newSessionId();
            sessionStorage.setItem(SESSION_KEY, id);
        }
        return id;
    } catch {
        return 'no-session-storage';
    }
}


export function trackBatch(events, token) {
    if (!events || events.length === 0) {
        return Promise.resolve();
    }
    const sessionId = getAnalyticsSessionId();
    const headers = { 'Content-Type': 'application/json' };
    if (token) {
        headers['Authorization'] = 'Bearer ' + token;
    }
    const body = {
        events: events.map((e) => ({
            eventName: e.eventName,
            category: e.category,
            clientTimestamp: e.clientTimestamp || new Date().toISOString(),
            sessionId: e.sessionId || sessionId,
            properties: e.properties && typeof e.properties === 'object' ? e.properties : {}
        }))
    };
    return fetch('/api/analytics/track', {
        method: 'POST',
        headers,
        body: JSON.stringify(body)
    }).catch(() => {});
}
