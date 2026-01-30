import './Reserved.css';
import { useState, useEffect } from 'react';
import { useAuth } from '../AuthContext.js';
import { useNavigate } from 'react-router-dom';

function Reserved() {
    const { authenticatedUser } = useAuth();
    const [reserved, setReserved] = useState([]);
    const navigate = useNavigate();

    useEffect(() => { // Замени useState на useEffect!
        const fetchReserved = async () => {
            const response = await fetch(`/api/customer/blocked-offers`, {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + authenticatedUser.token
                }
            });

            if (!response.ok) {
                console.log("Failed to fetch");
                return;
            }

            const data = await response.json();
            setReserved(data);
        }

        fetchReserved();

    }, [authenticatedUser.token]) // Добавь зависимость

    const handleViewDetails = (offerId) => {
        navigate(`/check-details/${offerId}`);
    }

    return (
        <div className='reserved'>
            <h4>Зарезервированные</h4>
            <hr></hr>
            {reserved.length > 0 ? (
                <div className='reserved-offers'>
                    {reserved.map((offer) => (
                        <div key={offer.id} className="reserved-preview">
                            <h3>{offer.location}</h3>
                            <div className="reserved-details">
                                <p><strong>Тип:</strong> {offer.type}</p>
                                <p><strong>Цена:</strong> {offer.price} $</p>
                                <p><strong>Площадь:</strong> {offer.size} м^2</p>
                                <p><strong>Комнат:</strong> {offer.rooms}</p>
                                {offer.blocked && (
                                    <p className="blocked-info">Зарезервировано вами</p>
                                )}
                            </div>
                            <button
                                onClick={() => handleViewDetails(offer.id)}
                                className="view-details-btn"
                            >
                                Посмотреть детали
                            </button>
                        </div>
                    ))}
                </div>
            ) : (
                <p>У вас пока нет зарезервированных предложений.</p>
            )}

        </div>
    );
}

export default Reserved;