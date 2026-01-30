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
        <div className="account">
            <div>
                <h2 className="lead">С возвращением {authenticatedUser.username}!</h2>
                <p>Здесь вы можете управлять данными и настройками своего аккаунта. Ваш аккаунт это доступ ко всем функциям и услугам нашего агентства. Контролируйте свой профиль, обновляйте информацию и обеспечьте, чтобы работа с нашим сервисом была максимально персонализирована под ваши потребности.
                </p>
                <hr></hr>
                <div className="actions">
                    {role === "CUSTOMER" && (
                        <>
                            <div className="action">
                                <img src={heart}/>
                                <Link to='/favorites'>Избранное</Link>
                            </div>
                            <div className="action">
                                <img src={padlock}/>
                                <Link to='/reserved'>Бронь</Link>
                            </div>
                            <div className="action">
                                <img src={history}/>
                                <Link to={`/history/${role}`}>История</Link>
                            </div>
                        </>
                    )}

                    {role === "OWNER" && (
                        <>
                            <div className="action">
                                <img src={estates}/>
                                <Link className='link' to='/my-estates'>Моё имущество</Link>
                            </div>
                            <div className="action">
                                <img src={report}/>
                                <Link to='/report-estate'>Заявка</Link>
                            </div>
                            <div className="action">
                                <img src={history}/>
                                <Link to={`/history/${role}`}>История</Link>
                            </div>
                        </>
                    )}

                    {role === "AGENT" && (
                        <>
                            <div className="action">
                                <img src={addEvent}/>
                                <Link to='/add-slots'>Добавить</Link>
                            </div>
                            <div className="action">
                                <img src={reported}/>
                                <Link to='/reported-offers'>Заявки</Link>
                            </div>
                            <div className="action">
                                <img src={manage}/>
                                <Link to='/manage-offers'>Управление</Link>
                            </div>
                        </>
                    )}
                    {role === "ADMIN" && (

                    <div className="action">
                        <img src={manage}/>
                        <Link to='/admin-users'>Юзеры</Link>
                    </div>
            )}
                    <div className="action">
                        <img src={setting}/>
                        <Link to='/settings'>Настройки</Link>
                    </div>
                    <hr></hr>
                    <div className="meetings">
                        <img src={soon}/>
                        <div className="meeting-card">
                            {meetings.map((meeting) => (
                                <div className="card">
                                    <h5>{meeting.user}</h5>
                                    <hr></hr>
                                    <h6>{meeting.agent}</h6>
                                    <p>{new Date(meeting.date).toLocaleString().slice(0, -3)}</p>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Account;