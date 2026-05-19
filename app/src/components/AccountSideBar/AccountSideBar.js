import { useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../AuthContext';
import './AccountSideBar.css';

function AccountSideBar() {
    const [role, setRole] = useState('');
    const { authenticatedUser } = useAuth();
    const location = useLocation();

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

            if (response.ok) {
                const data = await response.json();
                setRole(data.role);
            } else {
                console.error('Failed to fetch user details:', response.statusText);
            }
        };

        fetchDetails();
    }, [authenticatedUser]);

    const isActive = (path) => location.pathname === path;

    return (
        <div className="sidebar">
            <ul>

                {role === 'CUSTOMER' && (
                    <>
                        <li>
                            <Link
                                to="/favorites"
                                className={isActive('/favorites') ? 'active' : ''}
                            >
                                Избранное
                            </Link>
                        </li>

                        <li>
                            <Link
                                to="/schedule-meeting"
                                className={isActive('/schedule-meeting') ? 'active' : ''}
                            >
                                Встречи
                            </Link>
                        </li>
                    </>
                )}

                {role === 'OWNER' && (
                    <>
                        <li>
                            <Link
                                to="/my-estates"
                                className={isActive('/my-estates') ? 'active' : ''}
                            >
                                Моё имущество
                            </Link>
                        </li>

                        <li>
                            <Link
                                to="/report-estate"
                                className={isActive('/report-estate') ? 'active' : ''}
                            >
                                Заявка
                            </Link>
                        </li>

                        <li>
                            <Link
                                to="/schedule-meeting"
                                className={isActive('/schedule-meeting') ? 'active' : ''}
                            >
                                Встречи
                            </Link>
                        </li>
                    </>
                )}

                {role === 'AGENT' && (
                    <>
                        <li>
                            <Link
                                to="/add-slots"
                                className={isActive('/add-slots') ? 'active' : ''}
                            >
                                Добавить слоты
                            </Link>
                        </li>

                        <li>
                            <Link
                                to="/reported-offers"
                                className={isActive('/reported-offers') ? 'active' : ''}
                            >
                                Заявки
                            </Link>
                        </li>

                        <li>
                            <Link
                                to="/manage-offers"
                                className={isActive('/manage-offers') ? 'active' : ''}
                            >
                                Управление
                            </Link>
                        </li>
                    </>
                )}

                {role === 'ADMIN' && (
                    <li>
                        <Link
                            to="/admin-users"
                            className={isActive('/admin-users') ? 'active' : ''}
                        >
                            Пользователи
                        </Link>
                    </li>
                )}

                <li>
                    <Link
                        to="/settings"
                        className={isActive('/settings') ? 'active' : ''}
                    >
                        Настройки
                    </Link>
                </li>

            </ul>
        </div>
    );
}

export default AccountSideBar;