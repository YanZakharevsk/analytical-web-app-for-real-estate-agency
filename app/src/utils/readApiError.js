/**
 * Извлекает человекочитаемое сообщение из тела ответа API (Spring, поле response и др.).
 * Не возвращает сырой JSON целиком.
 */
function pickStructuredError(json) {
    if (!json || typeof json !== 'object') {
        return null;
    }
    for (const key of ['message', 'response', 'error', 'detail', 'title']) {
        const v = json[key];
        if (typeof v === 'string' && v.trim()) {
            return v.trim();
        }
    }
    if (Array.isArray(json.errors)) {
        const parts = json.errors
            .map((e) => {
                if (typeof e === 'string') {
                    return e;
                }
                if (e && typeof e.defaultMessage === 'string') {
                    return e.defaultMessage;
                }
                if (e && typeof e.message === 'string') {
                    return e.message;
                }
                return null;
            })
            .filter(Boolean);
        if (parts.length) {
            return parts.join('; ');
        }
    }
    if (json.errors && typeof json.errors === 'object' && !Array.isArray(json.errors)) {
        const parts = Object.entries(json.errors).map(([field, err]) => {
            if (Array.isArray(err)) {
                return `${field}: ${err.join(', ')}`;
            }
            return `${field}: ${err}`;
        });
        if (parts.length) {
            return parts.join('; ');
        }
    }
    if (Array.isArray(json.violations)) {
        const parts = json.violations
            .map((v) => (v && typeof v.message === 'string' ? v.message : null))
            .filter(Boolean);
        if (parts.length) {
            return parts.join('; ');
        }
    }
    return null;
}

export function formatApiErrorFromBody(text, fallbackStatus = '') {
    if (!text || !String(text).trim()) {
        return fallbackStatus || 'Ошибка запроса';
    }
    const s = String(text);
    try {
        const json = JSON.parse(s);
        const msg = pickStructuredError(json);
        if (msg) {
            return msg;
        }
    } catch {
        /* не JSON */
    }
    if (s.length > 500) {
        return `${s.slice(0, 500)}…`;
    }
    return s;
}

export async function readApiErrorMessage(response) {
    const text = await response.text();
    return formatApiErrorFromBody(text, response.statusText || '');
}
