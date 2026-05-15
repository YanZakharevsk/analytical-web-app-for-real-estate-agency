import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../AuthContext';
import './AccountSideBar.css';

function AccountSideBar() {
    const [role, setRole] = useState('');
    const { authenticatedUser } = useAuth();

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

    return (
        <div className="sidebar">
            <ul>
                {role === 'CUSTOMER' && (
                    <>
                        <li>
                            <Link to="/favorites">Избранное</Link>
                        </li>
                        <li>
                            <Link to="/schedule-meeting">Встречи</Link>
                        </li>
                    </>
                )}
                {role === 'OWNER' && (
                    <>
                        <li>
                            <Link to="/my-estates">Моё имущество</Link>
                        </li>
                        <li>
                            <Link to="/report-estate">Заявка</Link>
                        </li>
                        <li>
                            <Link to="/schedule-meeting">Встречи</Link>
                        </li>
                    </>
                )}
                {role === 'AGENT' && (
                    <>
                        <li>
                            <Link to="/add-slots">Добавить слоты</Link>
                        </li>
                        <li>
                            <Link to="/reported-offers">Заявки</Link>
                        </li>
                        <li>
                            <Link to="/manage-offers">Управление</Link>
                        </li>
                    </>
                )}
                {role === 'ADMIN' && (
                    <li>
                        <Link to="/admin-users">Пользователи</Link>
                    </li>
                )}
                <li>
                    <Link to="/settings">Настройки</Link>
                </li>
            </ul>
        </div>
    );
}

export default AccountSideBar;
