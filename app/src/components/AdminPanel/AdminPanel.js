import { useEffect, useState, useCallback } from 'react';
import { useAuth } from '../AuthContext';
import './AdminPanel.css';
import { readApiErrorMessage } from '../../utils/readApiError.js';

const ROLE_LABELS = {
    AGENT: 'Агент',
    OWNER: 'Продавец',
    CUSTOMER: 'Покупатель',
};

function AdminPanel() {
    const { authenticatedUser } = useAuth();
    const [users, setUsers] = useState([]);
    const [edits, setEdits] = useState({});
    const [rowMessage, setRowMessage] = useState({});
    const [globalError, setGlobalError] = useState('');

    const loadUsers = useCallback(async () => {
        setGlobalError('');
        try {
            const response = await fetch('/api/admin/users', {
                headers: {
                    Authorization: 'Bearer ' + authenticatedUser.token,
                },
            });
            if (!response.ok) {
                setGlobalError(await readApiErrorMessage(response));
                setUsers([]);
                return;
            }
            const data = await response.json();
            const filtered = (Array.isArray(data) ? data : []).filter((u) => u.role !== 'ADMIN');
            setUsers(filtered);
            const nextEdits = {};
            filtered.forEach((u) => {
                nextEdits[u.id] = { username: u.username, role: u.role };
            });
            setEdits(nextEdits);
        } catch (err) {
            console.error(err);
            setGlobalError('Не удалось загрузить список пользователей.');
        }
    }, [authenticatedUser.token]);

    useEffect(() => {
        if (!authenticatedUser?.token) {
            return;
        }
        loadUsers();
    }, [authenticatedUser.token, loadUsers]);

    const setEdit = (id, patch) => {
        setEdits((prev) => ({
            ...prev,
            [id]: { ...prev[id], ...patch },
        }));
        setRowMessage((m) => ({ ...m, [id]: '' }));
    };

    const saveUser = async (id) => {
        const base = users.find((u) => u.id === id);
        const e = edits[id];
        if (!base || !e) {
            return;
        }
        const body = {};
        const uTrim = (e.username ?? '').trim();
        if (uTrim && uTrim !== base.username) {
            body.username = uTrim;
        }
        if (e.role && e.role !== base.role) {
            body.role = e.role;
        }
        if (!Object.keys(body).length) {
            setRowMessage((m) => ({ ...m, [id]: 'Нет изменений для сохранения.' }));
            return;
        }

        setRowMessage((m) => ({ ...m, [id]: '' }));
        try {
            const response = await fetch('/api/admin/users/' + id, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: 'Bearer ' + authenticatedUser.token,
                },
                body: JSON.stringify(body),
            });
            //if (!response.ok) {
              //  setRowMessage((m) => ({ ...m, [id]: await readApiErrorMessage(response) }));
                //return;
            //}
            const updated = await response.json();
            setUsers((list) => list.map((u) => (u.id === id ? { ...u, username: updated.username, role: updated.role } : u)));
            setEdits((prev) => ({
                ...prev,
                [id]: { username: updated.username, role: updated.role },
            }));
            setRowMessage((m) => ({ ...m, [id]: 'Сохранено.' }));
        } catch (err) {
            console.error(err);
            setRowMessage((m) => ({ ...m, [id]: 'Ошибка сети.' }));
        }
    };

    const deleteUser = async (id) => {
        if (!window.confirm('Удалить этого пользователя? Это действие необратимо.')) {
            return;
        }
        try {
            const response = await fetch('/api/admin/users/' + id + '/delete', {
                method: 'POST',
                headers: {
                    Authorization: 'Bearer ' + authenticatedUser.token,
                },
            });
           // if (!response.ok) {
             //   setRowMessage((m) => ({ ...m, [id]: await readApiErrorMessage(response) }));
               // return;
            //}
            setUsers((list) => list.filter((u) => u.id !== id));
            setEdits((prev) => {
                const n = { ...prev };
                delete n[id];
                return n;
            });
        } catch (err) {
            console.error(err);
        }
    };

    return (
        <div className="re-page">
            <section className="re-hero">
                <h1>Пользователи</h1>
                <p>Изменение логина и роли по одному пользователю. Учётная запись администратора не отображается.</p>
            </section>
            <div className="re-inner">
                {globalError && <div className="re-alert re-alert-error admin-global-error">{globalError}</div>}
                <div className="re-table-wrap admin-table-wrap">
                    <table className="re-table">
                        <thead>
                            <tr>
                                <th>Логин</th>
                                <th>Роль</th>
                                <th>Статус</th>
                                <th style={{ minWidth: '200px' }}>Действия</th>
                            </tr>
                        </thead>
                        <tbody>
                            {users.map((u) => {
                                const e = edits[u.id] || { username: u.username, role: u.role };
                                return (
                                    <tr key={u.id}>
                                        <td>
                                            <input
                                                className="admin-input-username"
                                                value={e.username ?? ''}
                                                onChange={(ev) => setEdit(u.id, { username: ev.target.value })}
                                                aria-label={'Логин ' + u.id}
                                            />
                                        </td>
                                        <td>
                                            <select
                                                className="admin-select-role"
                                                value={e.role ?? u.role}
                                                onChange={(ev) => setEdit(u.id, { role: ev.target.value })}
                                            >
                                                <option value="AGENT">{ROLE_LABELS.AGENT}</option>
                                                <option value="OWNER">{ROLE_LABELS.OWNER}</option>
                                                <option value="CUSTOMER">{ROLE_LABELS.CUSTOMER}</option>
                                            </select>
                                        </td>
                                        <td>
                                            <span className="re-badge">Активен</span>
                                        </td>
                                        <td>
                                            <div className="admin-actions">
                                                <button type="button" className="re-btn-primary admin-btn-sm" onClick={() => saveUser(u.id)}>
                                                    Сохранить
                                                </button>
                                                <button type="button" className="re-btn-secondary admin-btn-sm" onClick={() => deleteUser(u.id)}>
                                                    Удалить
                                                </button>
                                            </div>
                                            {rowMessage[u.id] && <p className="admin-row-msg">{rowMessage[u.id]}</p>}
                                        </td>
                                    </tr>
                                );
                            })}
                        </tbody>
                    </table>
                </div>
                {users.length === 0 && !globalError && <p className="re-muted">Нет пользователей для отображения.</p>}
            </div>
        </div>
    );
}

export default AdminPanel;
