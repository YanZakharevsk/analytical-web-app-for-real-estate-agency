import { useEffect, useState } from 'react';
import { useAuth } from '../AuthContext.js';
import { useNavigate } from 'react-router-dom'; // Добавляем навигацию
import './Favorites.css';

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
        }

        fetchFavorites();
    }, [])

    // Функция для перехода к деталям предложения
    const handleViewDetails = (offerId) => {
        navigate(`/check-details/${offerId}`);
    }

    return (
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
    );
}

export default Favorites;