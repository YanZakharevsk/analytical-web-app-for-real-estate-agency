import { useAuth } from '../AuthContext';
import { useState, useEffect } from 'react';
import './ReportedOffers.css';
import PhotosFetcher from '../../PhotosFetcher';
import { Carousel } from 'react-responsive-carousel';
import "react-responsive-carousel/lib/styles/carousel.min.css";

function ReportedOffers() {
    const { authenticatedUser } = useAuth();
    const [reportedEstates, setReportedEstates] = useState([]);
    const [isOfferPosted, setIsOfferPosted] = useState(false);
    const [isOfferFailed, setIsOfferFailed] = useState(false);
    const [description, setDescription] = useState();
    const [price, setPrice] = useState();
    const [successMsg, setSuccessMsg] = useState('');
    const [errorMsg, setErrorMsg] = useState('');

    useEffect(() => {
        const fetchReportedEstates = async () => {
            const response = await fetch('/api/agent/reported-estates', {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + authenticatedUser.token
                }
            });

            if (!response.ok) {
                console.log("Failed to retrieve reported estates");
                return;
            }

            const data = await response.json();

            const fetchPhotosForEstate = async (estate) => {
                setDescription(estate.description);
                setPrice(estate.offeredPrice);

                const photos = await PhotosFetcher(estate.id);
                return {
                    id: estate.id,
                    info: estate,
                    photos: photos
                };
            };

            const estates = await Promise.all(
                data.map(async (estate) => ({
                    id: estate.id,
                    info: estate,
                    photos: await PhotosFetcher(estate.id),
                    description: estate.description,
                    price: estate.offeredPrice
                }))
            );

            setReportedEstates(estates);
        };

        fetchReportedEstates();

    }, [authenticatedUser]);

    function numberWithCommas(x) {
        return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }

    const onConfirmClick = async (e, id) => {
        e.preventDefault();

        setSuccessMsg('');
        setErrorMsg('');

        const estate = reportedEstates.find(e => e.id === id);

        const body = {
            price: estate.price,
            description: estate.description
        };

        const response = await fetch(`/api/agent/post-offer?id=${id}`, {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + authenticatedUser.token,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(body)
        });

        if (!response.ok) {
            setErrorMsg('Ошибка при подтверждении объявления');
            return;
        }

        setSuccessMsg('Объявление успешно подтверждено');
        setReportedEstates(prev => prev.filter(e => e.id !== id));
    };

    const onRejectClick = async (id) => {
        setSuccessMsg('');
        setErrorMsg('');

        const response = await fetch(`/api/agent/reject-offer?id=${id}`, {
            method: "DELETE",
            headers: {
                "Authorization": "Bearer " + authenticatedUser.token
            }
        });

        if (!response.ok) {
            setErrorMsg('Ошибка при отклонении объявления');
            return;
        }

        setErrorMsg('Объявление отклонено');
        setReportedEstates(prev => prev.filter(e => e.id !== id));
    };




    return (
        <div className='reported-offers'>
            <h4>Управление зарегистрированными объектами недвижимости</h4>
            <hr></hr>
            {reportedEstates.length > 0 ? (
                <div className='offers'>
                    {reportedEstates.map((estate) => (
                        <div className='estate'>
                            <div id="carouselExampleSlidesOnly" class="carousel slide" data-ride="carousel">
                                <Carousel>
                                    {estate.photos.map((photo) => (
                                        <div>
                                            <img src={photo}></img>
                                        </div>
                                    ))}
                                </Carousel>
                            </div>
                            <div className='info'>
                                <h4>{estate.info.owner}</h4>
                                <div className='header'>
                                    <p>{estate.info.type.toLowerCase().replace("_", " ")} &bull;</p>
                                    <p>{estate.info.availability.toLowerCase().replace("_", " ")} &bull;</p>
                                    <p>{estate.info.condition.toLowerCase().replace("_", " ")}</p>
                                </div>
                                <h3>${numberWithCommas(estate.info.offeredPrice)}</h3>
                                <hr></hr>
                                <h5>Краткое описание недвижимости</h5>
                                <div className='parameters'>
                                    <p><span>{estate.info.bathrooms}</span> {estate.info.bathrooms === 1 ? "Ванная комната" : "Ванных комнаты"}</p>
                                    <p><span>{estate.info.rooms}</span> Комнаты</p>
                                    <p><span>{estate.info.size}m²</span> Площади</p>
                                    <p>Гараж {estate.info.garage == false ? "не включен" : "включен"}</p>
                                    <p>Балкон {estate.info.balcony == false ? "не включен" : "включен"}</p>
                                </div>
                                <div className='desc'>
                                    <p>Изменить описание</p>
                                    <input
                                        value={estate.description}
                                        onChange={(e) =>
                                            setReportedEstates(prev =>
                                                prev.map(el =>
                                                    el.id === estate.id
                                                        ? {...el, description: e.target.value}
                                                        : el
                                                )
                                            )
                                        }
                                    /></div>
                                <div className='price'>
                                    <p>Изменить цену</p>
                                    <input
                                        type="number"
                                        value={estate.price}
                                        onChange={(e) =>
                                            setReportedEstates(prev =>
                                                prev.map(el =>
                                                    el.id === estate.id
                                                        ? {...el, price: e.target.value}
                                                        : el
                                                )
                                            )
                                        }
                                    />
                                </div>
                            </div>
                            <div className='manage'>
                                <button className='btn confirm'
                                        onClick={(e) => onConfirmClick(e, estate.id)}>Подтвердить
                                </button>
                                <button className='btn reject' onClick={() => onRejectClick(estate.id)}>
                                    Отклонить
                                </button>

                            </div>
                            <hr></hr>
                        </div>
                    ))}
                </div>
            ) : (
                <div>
                    <p>Нет объявлений о продаже недвижимости, ожидающих публикации.</p>
                </div>
            )}
            {successMsg && <div className="offer-msg success">{successMsg}</div>}
            {errorMsg && <div className="offer-msg error">{errorMsg}</div>}
        </div>
    );
}

export default ReportedOffers;