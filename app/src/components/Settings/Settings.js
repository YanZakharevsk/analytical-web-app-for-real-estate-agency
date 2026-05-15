import './Settings.css';
import { useEffect, useState, useCallback } from 'react';
import { useAuth } from '../AuthContext';
import user from './user.png';
import padlock from './padlock.png';
import pass from './pass.png';
import { readApiErrorMessage } from '../../utils/readApiError.js';

function Settings() {
    const { authenticatedUser, setAuthenticatedUser } = useAuth();
    const [details, setDetails] = useState(null);

    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [email, setEmail] = useState('');
    const [phone, setPhone] = useState('');
    const [username, setUsername] = useState('');

    const [currentPassword, setCurrentPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [repeatPassword, setRepeatPassword] = useState('');

    const [credentialsOk, setCredentialsOk] = useState(false);
    const [credentialsErr, setCredentialsErr] = useState('');
    const [passwordOk, setPasswordOk] = useState(false);
    const [passwordErr, setPasswordErr] = useState('');

    const applyDetails = useCallback((data) => {
        if (!data) {
            return;
        }
        setDetails(data);
        setFirstName(data.firstName ?? '');
        setLastName(data.lastName ?? '');
        setEmail(data.email ?? '');
        setPhone(data.phoneNumber ?? '');
        setUsername(data.username ?? '');
    }, []);

    useEffect(() => {
        const fetchDetails = async () => {
            if (!authenticatedUser?.token) {
                return;
            }
            const response = await fetch('/api/user-details', {
                method: 'GET',
                headers: {
                    Authorization: 'Bearer ' + authenticatedUser.token,
                },
            });

            if (!response.ok) {
                return;
            }
            const data = await response.json();
            applyDetails(data);
        };

        fetchDetails();
    }, [authenticatedUser?.token, applyDetails]);

    const buildCredentialsPatch = () => {
        if (!details) {
            return null;
        }
        const body = {};
        if (firstName.trim() !== (details.firstName ?? '')) {
            body.firstName = firstName.trim();
        }
        if (lastName.trim() !== (details.lastName ?? '')) {
            body.lastName = lastName.trim();
        }
        if (email.trim() !== (details.email ?? '')) {
            body.email = email.trim();
        }
        if (phone.trim() !== (details.phoneNumber ?? '')) {
            body.phoneNumber = phone.trim();
        }
        if (username.trim() !== (details.username ?? '')) {
            body.username = username.trim();
        }
        return Object.keys(body).length ? body : null;
    };

    const onCredentialsSubmit = (e) => {
        e.preventDefault();
        setCredentialsErr('');
        setCredentialsOk(false);
        setPasswordErr('');
        setPasswordOk(false);

        const body = buildCredentialsPatch();
        if (!body) {
            setCredentialsErr('Измените хотя бы одно поле, чтобы сохранить.');
            return;
        }

        const updateCredentials = async () => {
            const response = await fetch('/api/update-credentials', {
                method: 'PATCH',
                headers: {
                    Authorization: 'Bearer ' + authenticatedUser.token,
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(body),
            });

            if (!response.ok) {
                setCredentialsErr(await readApiErrorMessage(response));
                return;
            }

            const refreshed = await fetch('/api/user-details', {
                method: 'GET',
                headers: { Authorization: 'Bearer ' + authenticatedUser.token },
            });
            if (refreshed.ok) {
                const data = await refreshed.json();
                applyDetails(data);
            }
            if (body.username && body.username !== authenticatedUser.username) {
                setAuthenticatedUser({
                    username: body.username,
                    token: authenticatedUser.token,
                });
            }
            setCredentialsOk(true);
        };

        updateCredentials();
    };

    const onPasswordSubmit = (e) => {
        e.preventDefault();
        setPasswordErr('');
        setPasswordOk(false);
        setCredentialsErr('');
        setCredentialsOk(false);

        if (newPassword !== repeatPassword) {
            setPasswordErr('Новый пароль и подтверждение не совпадают.');
            return;
        }

        const updatePasswords = async () => {
            const response = await fetch('/api/update-password', {
                method: 'PATCH',
                headers: {
                    Authorization: 'Bearer ' + authenticatedUser.token,
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    oldPassword: currentPassword,
                    newPassword: newPassword,
                }),
            });

            if (!response.ok) {
                setPasswordErr(await readApiErrorMessage(response));
                return;
            }

            setPasswordOk(true);
            setCurrentPassword('');
            setNewPassword('');
            setRepeatPassword('');
        };

        updatePasswords();
    };

    return (
        <div className="re-page">
            <section className="re-hero">
                <h2>Настройки аккаунта</h2>
                <p>Обновляйте отдельные поля — отправляются только изменённые данные.</p>
            </section>
            <div className="re-inner">
                <div className="settings-layout">
                    <div className="re-surface settings-profile">
                        <div className="account-info">
                            <img src={user} alt="" width={56} height={56} />
                            <div className="info">
                                {details && (
                                    <h3>
                                        {details.firstName} {details.lastName}
                                    </h3>
                                )}
                                {details && <p className="re-muted">{details.email}</p>}
                                {details && <p className="re-muted">{details.phoneNumber}</p>}
                            </div>
                        </div>
                    </div>

                    <div className="re-surface">
                        <div className="credentials-header">
                            <h3 className="re-section-title">Учётные данные</h3>
                            <img src={pass} alt="" width={28} height={28} />
                        </div>
                        <div className="re-form-grid">
                            <div className="re-field">
                                <label htmlFor="firstName">Имя</label>
                                <input id="firstName" type="text" value={firstName} onChange={(e) => setFirstName(e.target.value)} />
                            </div>
                            <div className="re-field">
                                <label htmlFor="lastName">Фамилия</label>
                                <input id="lastName" type="text" value={lastName} onChange={(e) => setLastName(e.target.value)} />
                            </div>
                            <div className="re-field">
                                <label htmlFor="username">Логин</label>
                                <input id="username" type="text" value={username} onChange={(e) => setUsername(e.target.value)} />
                            </div>
                            <div className="re-field">
                                <label htmlFor="email">Email</label>
                                <input id="email" type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
                            </div>
                            <div className="re-field">
                                <label htmlFor="phone">Телефон (+375XXXXXXXXX)</label>
                                <input id="phone" type="text" value={phone} onChange={(e) => setPhone(e.target.value)} />
                            </div>
                        </div>
                        <div className="re-btn-row">
                            <button type="button" className="re-btn-primary" onClick={onCredentialsSubmit}>
                                Сохранить изменения
                            </button>
                        </div>
                        {credentialsOk && <div className="re-alert re-alert-success">Данные профиля обновлены.</div>}
                        {credentialsErr && <div className="re-alert re-alert-error">{credentialsErr}</div>}
                    </div>

                    <div className="re-surface">
                        <div className="credentials-header">
                            <h3 className="re-section-title">Пароль</h3>
                            <img src={padlock} alt="" width={28} height={28} />
                        </div>
                        <div className="re-form-grid">
                            <div className="re-field">
                                <label htmlFor="oldPassword">Текущий пароль</label>
                                <input
                                    id="oldPassword"
                                    type="password"
                                    value={currentPassword}
                                    onChange={(e) => setCurrentPassword(e.target.value)}
                                />
                            </div>
                            <div className="re-field">
                                <label htmlFor="newPassword">Новый пароль</label>
                                <input id="newPassword" type="password" value={newPassword} onChange={(e) => setNewPassword(e.target.value)} />
                            </div>
                            <div className="re-field">
                                <label htmlFor="repeatPassword">Повторите пароль</label>
                                <input
                                    id="repeatPassword"
                                    type="password"
                                    value={repeatPassword}
                                    onChange={(e) => setRepeatPassword(e.target.value)}
                                />
                            </div>
                        </div>
                        <div className="re-btn-row">
                            <button
                                type="button"
                                className="re-btn-primary"
                                disabled={!currentPassword || !newPassword || !repeatPassword}
                                onClick={onPasswordSubmit}
                            >
                                Сменить пароль
                            </button>
                        </div>
                        {passwordOk && <div className="re-alert re-alert-success">Пароль успешно изменён.</div>}
                        {passwordErr && <div className="re-alert re-alert-error">{passwordErr}</div>}
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Settings;
