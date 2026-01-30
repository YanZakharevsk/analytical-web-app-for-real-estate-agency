import { useAuth } from "../AuthContext"; 
import { useState, useEffect } from "react";
import "./MyEstates.css";
import JSZip from 'jszip';
import { Carousel } from 'react-responsive-carousel';
import "react-responsive-carousel/lib/styles/carousel.min.css"; 
import size from './size.png';
import bathroom from './bathroom.png';
import garage from './garage.png';
import room from './room.png';
import balcony from './balcony.png';
import storey from './storey.png';
import status from './status.png';

function MyEstates() {
    const { authenticatedUser } = useAuth();
    const [estates, setEstates] = useState([]);
    const [expandedDescriptions, setExpandedDescriptions] = useState({});


    const truncateText = (text, maxLength = 100) => {
        if (!text) return '';
        if (text.length <= maxLength) return text;
        return text.substring(0, maxLength) + '...';
    };

    const toggleDescription = (estateId) => {
        setExpandedDescriptions(prev => ({
            ...prev,
            [estateId]: !prev[estateId]
        }));

        // Плавная прокрутка к карточке после изменения высоты
        setTimeout(() => {
            const cardElement = document.querySelector(`.card[data-estate-id="${estateId}"]`);
            if (cardElement) {
                cardElement.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
            }
        }, 50);
    };

    const fetchPhotosFromZip = async (id) => {
        try {
            const response = await fetch(`/api/auth/photos?id=${id}`, {
                method: "GET"
            });

            if (!response.ok) {
                throw new Error('Failed to fetch photos');
            }

            const zipFileData = await response.arrayBuffer(); 
            const zip = await JSZip.loadAsync(zipFileData); 

            const photoUrls = [];

            zip.forEach((relativePath, zipEntry) => {
                if (zipEntry.dir) {
                    return; 
                }
                const photoData = zipEntry.async("blob").then(blob => URL.createObjectURL(blob));
                photoUrls.push(photoData);
            });

            return Promise.all(photoUrls);
        } catch (error) {
            console.error('Error fetching and displaying photos:', error);
            return [];
        }
    };

    useEffect(() => {
        const fetchEstates = async () => {
            const response = await fetch('/api/owner/my-estates', {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + authenticatedUser.token 
                }
            });

            if (!response.ok) {
                console.log("Failed to fetch estates");
                return;
            }

            const data = await response.json();

            const estatesWithPhotos = await Promise.all(data.map(async (estate) => {
                const photos = await fetchPhotosFromZip(estate.id);
                return {
                    id: estate.id,
                    data: estate,
                    photos: photos,
                    // document: await fetchDocument(estate.id)
                };
            }));
    
            setEstates(estatesWithPhotos);
        }

        fetchEstates();

        estates.forEach(async (estate) => {
            const photos = await fetchPhotosFromZip(estate.id);
            estate.photos = photos;
        });

    }, [authenticatedUser]);

    function numberWithCommas(x) {
        return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }


     return (
        <div className="my-estates">
            <h2 className="lead">Вот предложения, управление которыми вы доверили нам.</h2>
            <hr></hr>
            <div className="reported">
                {estates.map((estate) => (
                    <div className="card">
                        {!estate.data.isSubmitted && (
                            <button
                                className="delete-estate-btn"
                                onClick={async () => {
                                    await fetch(`/api/owner/delete-estate/${estate.id}`, {
                                        method: "DELETE",
                                        headers: {
                                            "Authorization": "Bearer " + authenticatedUser.token
                                        }
                                    });

                                    setEstates(prev => prev.filter(e => e.id !== estate.id));
                                }}
                            >
                                ✕
                            </button>
                        )}
                        <div id="carouselExampleSlidesOnly" class="carousel slide" data-ride="carousel">
                            <Carousel>
                                {estate.photos.map((photo) => (
                                    <div>
                                        <img src={photo}></img>
                                    </div>
                                ))}
                            </Carousel>
                        <div/>
                        </div>
                        <div className="info">
                            <div>
                                <div className="header">
                                    <h2>{estate.data.location}</h2>
                                    <h6><img src={status} width='20' height='20'/>{!estate.data.isSubmitted ? (
                                        <p>Статус: В ожидании</p>) : (<p>Статус: Рассмотрено</p>)}</h6>
                                </div>
                                <p>{estate.data.type.toLowerCase().replace("_", " ")} &bull;</p>
                                <p>{estate.data.availability.toLowerCase().replace("_", " ")} &bull;</p>
                                <p>{estate.data.condition.toLowerCase().replace("_", " ")}</p>
                                <hr></hr>
                            </div>
                            <p><img src={bathroom} width='40' height='40'/><span>{estate.data.bathrooms}</span> ванные
                                комнаты</p>
                            <p><img src={room} width='40' height='40'/><span>{estate.data.rooms}</span> комнаты</p>
                            <p><img src={garage} width='40'
                                    height='40'/>Гараж {estate.data.garage == true ? "включен" : "не включен"}</p>
                            <p><img src={storey} width='40'
                                    height='40'/><span>{estate.data.storey}</span> {estate.data.storey === 1 ? "этаж" : "этажа"}
                            </p>
                            <p><img src={size} width='40' height='40'/><span>{estate.data.size} m²</span> площади</p>
                            <p><img src={balcony} width='40'
                                    height='40'/>Балкон {estate.data.balcony == true ? "включен" : "не включен"}</p>
                            <p className="description">
                                <span className="description-text">
                                    <em>
                                        {expandedDescriptions[estate.id]
                                            ? estate.data.description
                                            : truncateText(estate.data.description)}
                                    </em>
                                </span>
                                {estate.data.description && estate.data.description.length > 255 && (
                                    <button
                                        onClick={() => toggleDescription(estate.id)}
                                        className="show-more-btn"
                                    >
                                        {expandedDescriptions[estate.id] ? 'Свернуть' : 'Показать больше'}
                                    </button>
                                )}
                            </p>
                            <p className="price">${numberWithCommas(estate.data.offeredPrice)}</p>
                        </div>
                    </div>
                ))}
            </div>
        </div>
     );
}

export default MyEstates;