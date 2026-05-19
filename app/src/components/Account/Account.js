import AccountSideBar from "../AccountSideBar/AccountSideBar";
import "./Account.css";
import { useAuth } from '../AuthContext.js'
import estates from './estates.png';
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import setting from './settings.png';
import report from './report.png';
import heart from './heart.png';
import padlock from './padlock.png';
import manage from './manager.png';
import addEvent from './add-event.png';
import reported from './reported.png';
import soon from './coming-soon.png';
import history from './transaction-history.png';

function Account() {
    const [role, setRole] = useState("");
    const { authenticatedUser } = useAuth();
    const [meetings, setMeetings] = useState([]);

    useEffect(() => {
        const fetchDetails = async () => {
            const response = await fetch('/api/user-details', {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + authenticatedUser.token
                }
            });

            if (response.ok) {
                const data = await response.json();
                setRole(data.role);
            } else {
                console.error('Failed to fetch user details:', response.statusText);
            }
        }

        fetchDetails();

    }, [authenticatedUser]);

    useEffect(() => {
        const fetchMeetings = async () => {
            if (role === 'CUSTOMER' || role === 'OWNER') {
                const response = await fetch('/api/scheduled-meetings', {
                    method: "GET",
                    headers: {
                        "Authorization": "Bearer " + authenticatedUser.token
                    }
                });

                if (!response.ok) {
                    return;
                }

                const data = await response.json();
                setMeetings(data);

            } else if (role === "AGENT") {
                const response = await fetch('/api/agent/scheduled-meetings', {
                    method: "GET",
                    headers: {
                        "Authorization": "Bearer " + authenticatedUser.token
                    }
                });

                if (!response.ok) {
                    return;
                }

                const data = await response.json();
                setMeetings(data);
            };
        }

        fetchMeetings();

    }, [role]);

    return (
        <div className="re-page re-page--transparent">
            <section className="re-hero">
                <h2>Личный кабинет</h2>
                <p>
                    С возвращением, <strong>{authenticatedUser.username}</strong>!
                </p>
            </section>

            <div className="re-inner">
                <div className="re-surface account-surface">
                    <div className="account">

                        <div>
                            <p className="re-muted">
                                Здесь вы можете управлять данными и настройками своего аккаунта.
                                Ваш аккаунт — это доступ ко всем функциям и услугам агентства.
                            </p>

                            <hr className="re-hr" />

                            <div className="actions">

                                {role === "CUSTOMER" && (
                                    <>
                                        <div className="action">
                                            <img src={heart} alt="favorites" />
                                            <Link to='/favorites'>Избранное</Link>
                                        </div>

                                        <div className="action">
                                            <img src={padlock} alt="reserved" />
                                            <Link to='/reserved'>Бронь</Link>
                                        </div>

                                        <div className="action">
                                            <img src={history} alt="history" />
                                            <Link to={`/history/${role}`}>История</Link>
                                        </div>
                                    </>
                                )}

                                {role === "OWNER" && (
                                    <>
                                        <div className="action">
                                            <img src={estates} alt="estates" />
                                            <Link className='link' to='/my-estates'>
                                                Моё имущество
                                            </Link>
                                        </div>

                                        <div className="action">
                                            <img src={report} alt="report" />
                                            <Link to='/report-estate'>Заявка</Link>
                                        </div>

                                        <div className="action">
                                            <img src={history} alt="history" />
                                            <Link to={`/history/${role}`}>История</Link>
                                        </div>
                                    </>
                                )}

                                {role === "AGENT" && (
                                    <>
                                        <div className="action">
                                            <img src={addEvent} alt="add" />
                                            <Link to='/add-slots'>Добавить</Link>
                                        </div>

                                        <div className="action">
                                            <img src={reported} alt="reported" />
                                            <Link to='/reported-offers'>Заявки</Link>
                                        </div>

                                        <div className="action">
                                            <img src={manage} alt="manage" />
                                            <Link to='/manage-offers'>Управление</Link>
                                        </div>
                                    </>
                                )}

                                {role === "ADMIN" && (
                                    <div className="action">
                                        <img src={manage} alt="admin" />
                                        <Link to='/admin-users'>Юзеры</Link>
                                    </div>
                                )}

                                <div className="action">
                                    <img src={setting} alt="settings" />
                                    <Link to='/settings'>Настройки</Link>
                                </div>
                            </div>

                            <hr className="re-hr" />

                            <div className="meetings">
                                <img src={soon} alt="meetings" />

                                {meetings.length > 0 ? (
                                    <div className="meeting-card">
                                        {meetings.map((meeting, index) => (
                                            <div className="card" key={index}>
                                                <h5>{meeting.user}</h5>
                                                <hr />

                                                <h6>{meeting.agent}</h6>

                                                <p>
                                                    {new Date(meeting.date)
                                                        .toLocaleString()
                                                        .slice(0, -3)}
                                                </p>
                                            </div>
                                        ))}
                                    </div>
                                ) : (
                                    <div className="empty-meetings">
                                        <h5>Запланированных встреч пока нет</h5>
                                        <p>
                                            Здесь будут отображаться ваши будущие встречи и показы недвижимости.
                                        </p>
                                    </div>
                                )}
                            </div>

                        </div>

                    </div>
                </div>
            </div>
        </div>
    );
}

export default Account;