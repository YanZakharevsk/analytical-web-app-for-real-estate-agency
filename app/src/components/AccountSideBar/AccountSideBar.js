import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../AuthContext";
import "./AccountSideBar.css";

function AccountSideBar() {
    const [role, setRole] = useState("");
    const { authenticatedUser } = useAuth();

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

    return (
        <div className="sidebar"> 
            <ul>
                {role === "CUSTOMER" && (
                    <>
                        <li><Link path='/liked-offers'>Избранное</Link></li>
                        <li><Link path='/scheduled-meetings'>Встречи</Link></li>
                        <li><Link path='/review-offer'>Отзывы</Link></li>
                    </>
                )}
                {role === "OWNER" && (
                    <>
                        <li><Link to='/my-estates'>Моё имущество</Link></li>
                        <li><Link to='/report-estate'>Заявка</Link></li>
                    </>
                )}
                {role === "AGENT" && (
                    <>
                        <li><Link to='/add-slots'>Добавить</Link></li>
                        <li><Link to='/reported-offers'>Заявки</Link></li>
                        <li><Link to='/manage-offers'>Управление</Link></li>
                    </>
                )}
                {role === "ADMIN" && (
                    <div>
                    </div>
                )}
                {role === "CUSTOMER" || role === "OWNER" && (
                    <>
                    <li><Link path='/schedule-meeting'>Встречи</Link></li>
                    </>
                )}
                <li><Link path='settings'>Настройки</Link></li>
            </ul>
            

        </div>
    );
}

export default AccountSideBar;
