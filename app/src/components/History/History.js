import { useAuth } from '../AuthContext';
import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import "./History.css";
import { Carousel } from 'react-responsive-carousel';
import "react-responsive-carousel/lib/styles/carousel.min.css";
import PhotosFetcher from '../../PhotosFetcher';
import bath from '../OfferCard/bath.png';
import room from '../OfferCard/room.png';
import balcony from '../OfferCard/balcony.png';
import garage from '../OfferCard/garage.png';
import size from '../OfferCard/size.png';
import stairs from '../OfferCard/stairs.png';
import income from './income.png';
import { Link } from 'react-router-dom';

function History() {
    const { authenticatedUser } = useAuth();
    const { role } = useParams();
    const [archived, setArchived] = useState([]);
    const [totalIncome, setTotalIncome] = useState(0);

    useEffect(() => {
        const fetchArchivedOffers = async () => {
            const response = await fetch(
                `/api/${String(role).toLowerCase()}/archived-offers`,
                {
                    method: "GET",
                    headers: {
                        "Authorization":
                            "Bearer " + authenticatedUser.token
                    }
                }
            );

            if (!response.ok) {
                console.log("Failed to fetch archived offers");
                return;
            }

            const data = await response.json();

            const estates = await Promise.all(
                data.map(async (estate) => {
                    const photos = await PhotosFetcher(
                        estate.estateID
                    );

                    return {
                        id: estate.id,
                        info: estate,
                        photos: photos
                    };
                })
            );

            setArchived(estates);
        };

        fetchArchivedOffers();
    }, [authenticatedUser, role]);

    useEffect(() => {
        const total = archived.reduce(
            (sum, offer) => sum + offer.info.price,
            0
        );
        setTotalIncome(total);
    }, [archived]);

    function numberWithCommas(x) {
        return x.toString().replace(
            /\B(?=(\d{3})+(?!\d))/g,
            ","
        );
    }

    return (
        <div className="re-page re-page--transparent">

            <section className="re-hero">
                <h2>История</h2>
                <p>Завершённые сделки.</p>
            </section>

            <div className="re-inner">

                <div className="re-surface">

                    <div className="history">

                        <h4>История транзакций</h4>

                        <hr />

                        {archived.length > 0 ? (
                            <div>

                                {role === 'OWNER' && (
                                    <div className="income">
                                        <img
                                            src={income}
                                            alt="income"
                                        />
                                        <span>
                                            Общий доход: $
                                            {numberWithCommas(
                                                totalIncome
                                            )}
                                        </span>
                                    </div>
                                )}

                                {archived.map((estate) => (
                                    <div
                                        className="estate"
                                        key={estate.id}
                                    >

                                        <div className="carousel-wrapper">
                                            <Carousel>
                                                {estate.photos.map(
                                                    (photo, idx) => (
                                                        <div key={idx}>
                                                            <img
                                                                src={photo}
                                                                alt="estate"
                                                            />
                                                        </div>
                                                    )
                                                )}
                                            </Carousel>
                                        </div>

                                        <div className="info">

                                            <h4>
                                                {estate.info.location}
                                            </h4>

                                            <div className="header">
                                                <p>
                                                    {estate.info.type
                                                        .toLowerCase()
                                                        .replace("_", " ")}
                                                </p>
                                                <p>
                                                    {estate.info.availability
                                                        .toLowerCase()
                                                        .replace("_", " ")}
                                                </p>
                                                <p>
                                                    {estate.info.condition
                                                        .toLowerCase()
                                                        .replace("_", " ")}
                                                </p>
                                            </div>

                                            <h3>
                                                $
                                                {numberWithCommas(
                                                    estate.info.price
                                                )}
                                            </h3>

                                            <hr />

                                            <div className="parameters">

                                                <div className="col">
                                                    <img src={size} />
                                                    <p>
                                                        <span>
                                                            {estate.info.size}
                                                        </span>
                                                        m²
                                                    </p>

                                                    <img src={bath} />
                                                    <p>
                                                        <span>
                                                            {estate.info.bathrooms}
                                                        </span>
                                                    </p>

                                                    <img src={room} />
                                                    <p>
                                                        <span>
                                                            {estate.info.rooms}
                                                        </span>
                                                    </p>
                                                </div>

                                                <div className="col">
                                                    <img src={stairs} />
                                                    <p>
                                                        <span>
                                                            {estate.info.storey}
                                                        </span>
                                                    </p>

                                                    <img src={garage} />
                                                    <p>
                                                        {estate.info.garage
                                                            ? "Garage included"
                                                            : "No garage"}
                                                    </p>

                                                    <img src={balcony} />
                                                    <p>
                                                        {estate.info.balcony
                                                            ? "Balcony included"
                                                            : "No balcony"}
                                                    </p>
                                                </div>

                                            </div>

                                            {!estate.info.isReviewed && (
                                                <button>
                                                    <Link
                                                        to={`/review/${
                                                            estate.info.agentID
                                                        }/${role.toLowerCase()}/${
                                                            estate.id
                                                        }`}
                                                    >
                                                        Отзывы
                                                    </Link>
                                                </button>
                                            )}

                                        </div>

                                    </div>
                                ))}

                            </div>
                        ) : (
                            <div className="empty">
                                <p>
                                    Вы ещё не совершали сделок
                                </p>
                            </div>
                        )}

                    </div>

                </div>

            </div>

        </div>
    );
}

export default History;