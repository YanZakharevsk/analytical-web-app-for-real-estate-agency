import './Estates.css';
import { useState, useEffect } from 'react';
import PhotosFetcher from '../../PhotosFetcher.js';
import heart from './heart.png';
import redHeart from './heart-red.png';
import details from './file.png';
import filter from './filter.png';
import { Link } from 'react-router-dom';
import { useAuth } from '../AuthContext.js'; 

function Estates() {
    const [offers, setOffers] = useState([]);
    const { authenticatedUser } = useAuth();
    const [expandedDescriptions, setExpandedDescriptions] = useState({});
    const [criteria, setCriteria] = useState({
        type: '',
        bathrooms: null,
        rooms: null,
        garage: null,
        storey: null,
        location: '',
        balcony: null,
        availability: '',
        // size: [0, 500], 
        condition: '',
        // price: [0, 1000000], 
        // postFrom: null,
        // postTo: null
    });
    const [filters, setFilters] = useState(false);

    const types = [
        ['APARTMENT', 'Квартира'],
        ['BUNGALOW', 'Частный дом'],
        ['COTTAGE', 'Коттедж'],
        ['MANSION', 'Особняк']
    ];

    const availabilities = [
        ['FOR_SALE', 'Продажа'],
        ['FOR_RENT', 'Аренда']
    ];

    const conditions = [
        ['NEEDS_RENOVATION', 'Требует ремонта'],
        ['DEVELOPER_CONDITION', 'От застройщика'],
        ['AFTER_RENOVATION', 'После ремонта'],
        ['NORMAL_USE_SIGNS', 'В хорошем состоянии']
    ];

    const translateEnum = (value, array) => {
        if (!value) return '';
        const found = array.find(item => item[0] === value);
        return found ? found[1] : value;
    };

    useEffect(() => {
        const fetchOffers = async () => {
            const parameters = ["?"];
            
            Object.keys(criteria).forEach(key => {
                const value = criteria[key];

                if (value !== null && value !== "") {
                    parameters.push("&" + key + "=" + criteria[key]);
                }
            });

            try {
                const response = await fetch('/api/auth/offers' + parameters.join(""), {
                    method: "GET"
                });

                console.log(response)
    
                if (response.ok) {
                    const data = await response.json();

                    const fetchOffersWithPhotos = async (offer) => {
                        const photos = await PhotosFetcher(offer.estateID);
                        return {
                            info: offer,
                            photos: photos
                        }
                    };

                    setOffers(await Promise.all(data.filter((o) => o != null).map(fetchOffersWithPhotos)))
                    
                } else {
                    console.log("Failed to fetch offers");
                }
            } catch (error) {
                console.error("Error fetching offers:", error);
            }
        }
        fetchOffers();
    }, [criteria]);

    function numberWithCommas(x) {
        return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, " ");
    }

    const handleFilterChange = (e) => {
        const { name, value, type, checked } = e.target;

        if (type === 'checkbox') {
            setCriteria(prevCriteria => ({
                ...prevCriteria,
                [name]: checked
            }));
            return;
        }

        setCriteria(prev => ({
            ...prev, [name]: value
        }));
    }

    const addToFavorites = (id) => {

        if (authenticatedUser.token === "") {
            alert("You must log in first.");
            return;
        }

        const add = async () => {
            const response = await fetch(`/api/customer/add-to-favorites?id=${id}`, {
                method: "POST",
                headers: {
                    "Authorization": "Bearer " + authenticatedUser.token
                }
            });

            if (!response.ok) {
                console.log("Failed to add the offer to favorites");
                return;
            }

            const image = document.querySelector(`.heart${id}`);

            if (image) {
                image.setAttribute('src', redHeart); 
            }
        };

        add();
    }

    const toggleDescription = (id) => {
        setExpandedDescriptions(prev => ({
            ...prev,
            [id]: !prev[id]
        }));
    };


    const onCheckDetailsClick = () => {
        if (authenticatedUser.token === "") {
            alert("You must log in first.");
            return;
        }
    }

    return (
        <div className="estates">
            <h2 className="lead">Ознакомьтесь с эксклюзивными предложениями по продаже недвижимости.</h2>
            <p>
                Добро пожаловать в наш раздел «Недвижимость», где вы найдете не только объекты недвижимости, но и эксклюзивные предложения, которые значительно повысят ценность вашего опыта в сфере недвижимости.
                Погрузитесь в нашу тщательно подобранную коллекцию предложений, акций и специальных бонусов,
                разработанных для того, чтобы улучшить ваш опыт и помочь вам найти недвижимость вашей мечты.</p>
            <hr></hr>
            <div className='filters'>
                <img src={filter} alt={"Estate"} width='30' height='30' onClick={() => setFilters(!filters)}/>
                {filters && (
                    <div className='selectors'>
                        <div className='sel'>
                            <select name='availability' onChange={handleFilterChange}>
                                <option value="">Доступность</option>
                                {availabilities.map((availability) => (
                                    <option value={availability[0]}>{availability[1]}</option>
                                ))}
                            </select>
                            <select name='condition' onChange={handleFilterChange}>
                                <option value="">Условия</option>
                                {conditions.map((condition) => (
                                    <option value={condition[0]}>{condition[1]}</option>
                                ))}
                            </select>
                            <select name='type' onChange={handleFilterChange}>
                                <option value="">Тип</option>
                                {types.map((type) => (
                                    <option value={type[0]}>{type[1]}</option>
                                ))}
                            </select>
                        </div>
                        <div className='col'>
                            <label htmlFor='bathrooms'>Ванные комнаты</label>
                            <input type='number' min={1} onChange={handleFilterChange}></input>
                            <label htmlFor='storey'>Этаж</label>
                            <input type='number' min={1} onChange={handleFilterChange}></input>
                            <label htmlFor='rooms'>Комнаты</label>
                            <input type='number' min={1} onChange={handleFilterChange}></input>
                        </div>
                        <div className='sel'>
                            <div>
                                <label htmlFor='balcony'>Балкон</label>
                                <input type='checkbox' onChange={handleFilterChange}></input>
                            </div>
                            <div>
                                <label htmlFor='garage'>Гараж</label>
                                <input type='checkbox' onChange={handleFilterChange}></input>
                            </div>
                            <div>
                                <label htmlFor='location'>Местоположение</label>
                                <input type='text' onChange={handleFilterChange}></input>
                            </div>
                        </div>
                    </div>
                )}
            </div>
            <ul>
                {offers.length > 0 ? (offers.map((offer) => (
                    <li key={offer.id}>
                        <div className='card'>
                            <img className='estate-photo' src={offer.photos[0]}/>
                            <div className='info'>
                                <div>
                                    <h3>{offer.info.location} &bull; {translateEnum(offer.info.availability, availabilities)}</h3>                                    <h5></h5>
                                </div>
                                <hr></hr>
                                <h4>${numberWithCommas(offer.info.price)}</h4>
                                <div className="description">
                                    <p className={expandedDescriptions[offer.info.id] ? 'expanded' : ''}>
                                        {offer.info.description}
                                    </p>

                                    {offer.info.description.length > 120 && (
                                        <button
                                            className="toggle-desc"
                                            onClick={() => toggleDescription(offer.info.id)}
                                        >
                                            {expandedDescriptions[offer.info.id]
                                                ? 'Свернуть'
                                                : 'Показать больше'}
                                        </button>
                                    )}
                                </div>

                                <div className='buttons'>
                                    <button className='btn'><Link to={`/check-details/${offer.info.id}`}
                                                                  onClick={onCheckDetailsClick}><img src={details}
                                                                                                     width='25'
                                                                                                     height='25'/></Link>Просмотреть
                                        детали
                                    </button>
                                    <button className='btn' value={offer.id}
                                            onClick={() => addToFavorites(offer.info.id)}><img
                                        className={'heart' + offer.info.id} src={heart} width='25' height='25'/> Добавить в избранное</button>
                                </div>

                            </div>
                        </div>
                    </li>
                ))) : (
                    <p>Предложений не найдено. Попробуйте изменить критерии.</p>
                )}
            </ul>
        </div>
    );
}

export default Estates;

