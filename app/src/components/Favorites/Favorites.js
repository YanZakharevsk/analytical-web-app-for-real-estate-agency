import { useEffect, useState } from 'react';
import { useAuth } from '../AuthContext.js';
import { useNavigate } from 'react-router-dom'; // Добавляем навигацию
import './Favorites.css';
import { trackEvent, getCurrentScreenOrigin } from '../../utils/analyticsTrack';

function Favorites() {
    const { authenticatedUser } = useAuth();
    const [favorites, setFavorites] = useState([]);
    const navigate = useNavigate(); // Хук для навигации

    useEffect(() => {
        const fetchFavorites = async () => {
            const response = await fetch('/api/customer/favorites', {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + authenticatedUser.token
                }
            });

            if (!response.ok) {
                console.log("Failed to fetch favorites");
                return;
            }

            const data = await response.json();
            setFavorites(data);
            trackEvent({
                eventName: "favorites_sync_completed",
                category: "SYSTEM",
                properties: {
                    favorites_count: Array.isArray(data) ? data.length : 0
                }
            }, authenticatedUser?.token);
        }

        fetchFavorites();
    }, [])

    // Функция для перехода к деталям предложения
    const handleViewDetails = (offerId) => {
        trackEvent({
            eventName: "property_card_click",
            category: "INTERACTION",
            properties: {
                property_id: offerId,
                screen_origin: getCurrentScreenOrigin()
            }
        }, authenticatedUser?.token);
        navigate(`/check-details/${offerId}`);
    }

    return (
        <div className="re-page">
            <section className="re-hero">
                <h2>Избранное</h2>
                <p>Сохранённые объявления.</p>
            </section>
            <div className="re-inner">
        <div className='favorites'>
            <h4>Избранные</h4>
            <hr></hr>
            <div className='offers'>
                {favorites.length > 0 ? (
                    favorites.map((offer) => (
                        <div key={offer.id} className="offer-preview">
                            <h3>{offer.location}</h3>
                            <p><strong>Тип:</strong> {offer.type}</p>
                            <p><strong>Цена:</strong> {offer.price} $</p>
                            <p><strong>Площадь:</strong> {offer.size} м^2</p>
                            <button
                                onClick={() => handleViewDetails(offer.id)}
                                className="view-details-btn"
                            >
                                Посмотреть детали
                            </button>
                        </div>
                    ))
                ) : (
                    <p>В избранное пока не добавлено ни одного предложения.</p>
                )}
            </div>
        </div>
            </div>
        </div>
    );
}

export default Favorites;