import './Review.css';
import { useParams } from 'react-router-dom';
import { useState, useEffect } from 'react';
import { useAuth } from '../AuthContext.js';
import star from './star.png';
import starFilled from './star-filled.png';

function Review() {
    const { authenticatedUser } = useAuth();
    const { agent, role, id } = useParams();
    const [agentInfo, setAgentInfo] = useState();
    const [rating, setRating] = useState(0);
    const [comment, setComment] = useState("");
    const [stars, setStars] = useState(Array(10).fill(false));
    const [isSuccess, setIsSuccess] = useState(false);
    const [isFailed, setIsFailed] = useState(false);

    const onStarClick = (idx) => {
        const newStars = stars.map((_, index) =>
            index <= idx ? true : false
        );

        setStars(newStars);
        setRating(idx + 1);
    };
    
    useState(() => {
        const fetchAgent = async () => {
            const response = await fetch(`/api/auth/agent?id=${agent}`, {
                method: "GET",
            });

            if (!response.ok) {
                console.log("Failed to fetch the agent");
                return;
            }

            const data = await response.json();

            setAgentInfo(data);
        }

        fetchAgent();

    }, []);

    const onSubmitClick = (e) => {
        e.preventDefault();

        const postReview = async () => {
            const body = {
                rating: rating,
                comment: comment
            };

            const response = await fetch(`/api/review?id=${id}`, {
                method: "POST",
                headers: {
                    "Authorization": "Bearer " + authenticatedUser.token,
                    "Content-Type": "application/json"
                }, 
                body: JSON.stringify(body)
            });

            if (!response.ok) {
                console.log("Failed to review the offer");

                setIsSuccess(false);
                setIsFailed(true);

                return;
            };

            setIsSuccess(true);
            setIsFailed(false);
        }

        postReview();
    }

    return (
        <div className="review">
            {agentInfo != null && (
                <>
                    <div>
                        <h2 className='lead'>Расскажите другим о том, как прошло ваше сотрудничество с {agentInfo.fullName}  </h2>
                        <hr></hr>
                    </div>
                    <div className='rating'>
                        {stars.map((filled, idx) => (
                            <img key={idx} id={idx} src={filled ? starFilled : star} width='35' onClick={() => onStarClick(idx)}/>
                        ))}
                    </div>
                    <input type='text' placeholder='Поделитесь своим мнением!' onChange={(e) => setComment(e.target.value)}></input>
                    <button className='btn btn-dark' disabled={comment === "" || rating === 0} onClick={onSubmitClick}>Подтвердить</button>
                    {isSuccess && (
                        <div className='alert alert-success'>
                            Благодарим вас за ценный отзыв! Ваши комментарии помогают нам поддерживать высочайшие стандарты и гарантировать качество работы наших специалистов.
                        </div>
                    )}
                    {isFailed && (
                        <div className='alert alert-danger'>
                            Что-то пошло не так. Попробуйте ещё раз.
                        </div>
                    )}
                    
                </>
            )}
        </div>
    );
}

export default Review;