import { useAuth } from "../AuthContext";
import "./Login.css";
import { useState } from "react";
import { Link } from "react-router-dom";
import { trackEvent } from "../../utils/analyticsTrack";

function Login() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const { setAuthenticatedUser } = useAuth();
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [loginFailure, setLoginFailure] = useState(false);

    const handleSubmit = (e) => {
        e.preventDefault();
        setLoginFailure(false);

        if (!username.trim() || !password.trim()) {
            setLoginFailure(true);
            return;
        }

        trackEvent({
            eventName: "login_submit",
            category: "INTERACTION",
            properties: {
                user_email: username,
                timestamp: new Date().toISOString(),
                auth_method: "email"
            }
        });

        const authenticate = async () => {
            const response = await fetch('/api/auth/authenticate', {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    username,
                    password
                })
            });

            if (!response.ok) {
                setLoginFailure(true);

                trackEvent({
                    eventName: "auth_failed",
                    category: "SYSTEM",
                    properties: {
                        error_reason: "wrong_password_or_no_user",
                        user_email: username,
                        login_method: "email"
                    }
                });

                return;
            }

            const data = await response.json();

            setIsAuthenticated(true);

            setAuthenticatedUser({
                username,
                token: data.token
            });

            trackEvent({
                eventName: "login_success",
                category: "CONVERSION",
                properties: {
                    auth_type: "email",
                    user_role: data.role || "unknown"
                }
            }, data.token);
        };

        authenticate();
    };

    const clear = (e) => {
        e.preventDefault();
        setPassword("");
        setUsername("");
        setLoginFailure(false);
        setIsAuthenticated(false);
    };

    return (
        <div className="re-page re-page--transparent">

            <section className="re-hero">
                <h2>Вход</h2>
                <p>
                    Авторизуйтесь для доступа к избранному,
                    бронированию и личному кабинету.
                </p>
            </section>

            <div className="re-inner re-inner--narrow">

                <div className="re-surface">

                    <div className="login">

                        <h3>Авторизация</h3>

                        <hr />

                        <form onSubmit={(e) => e.preventDefault()}>

                            <label>Логин</label>
                            <input
                                type="text"
                                value={username}
                                onChange={(e) =>
                                    setUsername(e.target.value)
                                }
                            />

                            <label>Пароль</label>
                            <input
                                type="password"
                                value={password}
                                onChange={(e) =>
                                    setPassword(e.target.value)
                                }
                            />

                            <div className="btn-group">
                                <button
                                    className="btn btn-dark"
                                    onClick={handleSubmit}
                                    disabled={
                                        !username.trim() ||
                                        !password.trim()
                                    }
                                >
                                    Войти
                                </button>

                                <button
                                    className="btn btn-dark"
                                    onClick={clear}
                                >
                                    Очистить
                                </button>
                            </div>

                        </form>

                        {isAuthenticated && (
                            <div className="alert alert-success">
                                <p>Вы успешно вошли.</p>
                                <hr />
                                <p>
                                    Управление аккаунтом{' '}
                                    <Link to="/account">
                                        здесь
                                    </Link>
                                </p>
                            </div>
                        )}

                        {loginFailure && (
                            <div className="alert alert-danger">
                                Аутентификация не пройдена.
                                Попробуйте ещё раз.
                            </div>
                        )}

                    </div>

                </div>

            </div>

        </div>
    );
}

export default Login;