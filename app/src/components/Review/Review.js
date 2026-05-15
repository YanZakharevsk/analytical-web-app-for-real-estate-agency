import './Review.css';
import { useParams } from 'react-router-dom';
import { useState, useEffect } from 'react';
import { useAuth } from '../AuthContext.js';
import star from './star.png';
import starFilled from './star-filled.png';
import { trackEvent } from '../../utils/analyticsTrack';
import { readApiErrorMessage } from '../../utils/readApiError.js';

function Review() {
    const { authenticatedUser } = useAuth();
    const { agent, role, id } = useParams();
    const [agentInfo, setAgentInfo] = useState();
    const [rating, setRating] = useState(0);
    const [comment, setComment] = useState("");
    const [stars, setStars] = useState(Array(10).fill(false));
    const [isSuccess, setIsSuccess] = useState(false);
    const [isFailed, setIsFailed] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');

    const onStarClick = (idx) => {
        const newStars = stars.map((_, index) =>
            index <= idx ? true : false
        );

        setStars(newStars);
        setRating(idx + 1);
    };
    
    useEffect(() => {
        const fetchAgent = async () => {
            const response = await fetch(`/api/auth/agent?id=${agent}`, {
                method: 'GET',
            });

            if (!response.ok) {
                console.log('Failed to fetch the agent');
                return;
            }

            const data = await response.json();
            setAgentInfo(data);
        };

        if (agent) {
            fetchAgent();
        }
    }, [agent]);

    const onSubmitClick = (e) => {
        e.preventDefault();
        setErrorMessage('');
        setIsFailed(false);
        if (!authenticatedUser?.token) {
            setIsFailed(true);
            setErrorMessage('Войдите в систему, чтобы оставить отзыв.');
            return;
        }
        trackEvent({
            eventName: "review_submit",
            category: "INTERACTION",
            properties: {
                agent_id: Number(agent),
                rating: rating,
                comment_length: comment.length
            }
        }, authenticatedUser?.token);

        const postReview = async () => {
            const body = {
                rating: rating,
                comment: comment
            };

            const response = await fetch(`/api/review?id=${id}`, {
                method: 'POST',
                headers: {
                    Authorization: 'Bearer ' + authenticatedUser.token,
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(body),
            });

            if (!response.ok) {
                const msg = await readApiErrorMessage(response);
                setErrorMessage(msg);
                setIsSuccess(false);
                setIsFailed(true);
                trackEvent({
                    eventName: 'review_submit_failed',
                    category: 'SYSTEM',
                    properties: {
                        field_name: 'review',
                        error_type: 'submit_failed',
                    },
                }, authenticatedUser?.token);

                return;
            }

            setIsSuccess(true);
            setIsFailed(false);
            setErrorMessage('');
            trackEvent({
                eventName: "review_published",
                category: "CONVERSION",
                properties: {
                    agent_id: Number(agent),
                    rating: rating,
                    comment_length: comment.length
                }
            }, authenticatedUser?.token);
        }

        postReview();
    }

    return (
        <div className="re-page">
            <section className="re-hero">
                <h2>Отзыв об агенте</h2>
                <p>Оцените сотрудничество и оставьте комментарий.</p>
            </section>
            <div className="re-inner re-inner--narrow">
            <div className="re-surface">
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
                        <div className="alert alert-danger">
                            {errorMessage || 'Что-то пошло не так. Попробуйте ещё раз.'}
                        </div>
                    )}
                    
                </>
            )}
        </div>
            </div>
            </div>
        </div>
    );
}

export default Review;