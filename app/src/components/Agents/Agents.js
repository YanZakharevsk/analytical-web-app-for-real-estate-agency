import "./Agents.css";
import { useState, useEffect } from 'react';
import star from './star.png';
import { Link } from 'react-router-dom';
import calendar from './calendar.png';
import question from './question.png';
import { useAuth } from '../AuthContext';

function Agents() {
    const [agents, setAgents] = useState([]);
    const [calendarSlots, setCalendarSlots] = useState([]);
    const [isCalendarEmpty, setIsCalendarEmpty] = useState(false);
    const [selectedAgentId, setSelectedAgentId] = useState(null);

    const { authenticatedUser } = useAuth();

    useEffect(() => {
        const fetchAgents = async () => {
            const response = await fetch('/api/auth/agents', {
                method: "GET"
            });

            if (!response.ok) {
                console.log("Failed to fetch agents");
                return;
            }

            const data = await response.json();

            const agentsWithReviews = await Promise.all(
                data.map(async (agent) => {
                    const reviewsResponse = await fetch(
                        `/api/auth/reviews?id=${agent.id}`,
                        {
                            method: "GET"
                        }
                    );

                    if (reviewsResponse.ok) {
                        const reviews = await reviewsResponse.json();

                        return {
                            agentInfo: agent,
                            reviews: reviews
                        };
                    }

                    return {
                        agentInfo: agent,
                        reviews: []
                    };
                })
            );

            setAgents(agentsWithReviews);
        };

        fetchAgents();

    }, [calendarSlots]);

    const reviewArr = (n) => {
        return new Array(n).fill(null);
    };

    const checkCalendar = (id) => {
        setSelectedAgentId(id);

        const fetchCalendar = async () => {
            const response = await fetch(`/api/auth/calendar?id=${id}`, {
                method: "GET"
            });

            if (!response.ok) {
                setIsCalendarEmpty(true);
                setCalendarSlots([]);
                return;
            }

            const data = await response.json();

            if (data.length === 0) {
                setIsCalendarEmpty(true);
            } else {
                setIsCalendarEmpty(false);
            }

            setCalendarSlots(data);
        };

        fetchCalendar();
    };

    const scheduleMeeting = (slot) => {
        if (authenticatedUser.token === '') {
            alert("You have to log in first");
            return;
        }

        const schedule = async () => {
            const response = await fetch(
                `/api/schedule-meeting?id=${slot}`,
                {
                    method: "POST",
                    headers: {
                        "Authorization":
                            "Bearer " + authenticatedUser.token
                    }
                }
            );

            if (!response.ok) {
                console.log("Failed to schedule meeting");
                return;
            }

            setCalendarSlots((prev) =>
                prev.filter((s) => s.id !== slot)
            );
        };

        schedule();
    };

    return (
        <div className="re-page re-page--transparent">

            <section className="re-hero">
                <h2>Агенты</h2>
                <p>Команда экспертов Real Estate.</p>
            </section>

            <div className="re-inner">

                <div className="re-surface">

                    <div className="agents">

                        <h2 className="lead">
                            Наша превосходная команда экспертов
                        </h2>

                        <p>
                            Выберите из числа квалифицированных и опытных
                            агентов по недвижимости. Наша команда обладает
                            богатым опытом, который применяется в каждой
                            сделке, гарантируя уверенность, безопасность
                            и профессиональное сопровождение на каждом этапе.
                        </p>

                        <hr />

                        {agents.length > 0 && agents.map((agent, index) => (

                            <div className="card" key={index}>

                                <h2 className="lead">
                                    {agent.agentInfo.fullName}
                                </h2>

                                <hr />

                                <h6>
                                    <em>{agent.agentInfo.email}</em>
                                </h6>

                                <h6>
                                    <em>{agent.agentInfo.phoneNumber}</em>
                                </h6>

                                <hr />

                                <div className="actions">

                                    <div>
                                        <img
                                            src={calendar}
                                            alt="calendar"
                                        />

                                        <Link
                                            onClick={() =>
                                                checkCalendar(
                                                    agent.agentInfo.id
                                                )
                                            }
                                        >
                                            Проверить календарь
                                        </Link>
                                    </div>

                                    <div>
                                        <img
                                            src={question}
                                            alt="question"
                                        />

                                        <Link
                                            to={`/ask-question/${agent.agentInfo.id}`}
                                        >
                                            Задать вопрос
                                        </Link>
                                    </div>

                                </div>

                                {selectedAgentId === agent.agentInfo.id && (
                                    <>
                                        {calendarSlots.length > 0 && (
                                            <div className="available-slots">

                                                {calendarSlots.map((slot) => (

                                                    <div
                                                        className="slot"
                                                        key={slot.id}
                                                    >
                                                        <span>
                                                            {new Date(slot.date)
                                                                .toLocaleString()
                                                                .slice(0, -3)}
                                                        </span>

                                                        <button
                                                            onClick={() =>
                                                                scheduleMeeting(slot.id)
                                                            }
                                                        >
                                                            Забронировать
                                                        </button>
                                                    </div>

                                                ))}

                                            </div>
                                        )}

                                        {isCalendarEmpty && (
                                            <div className="empty-slots">
                                                У агента пока нет доступных слотов
                                            </div>
                                        )}
                                    </>
                                )}

                                <hr />

                                {agent.reviews.length > 0 ? (

                                    agent.reviews.map((review, idx) => (

                                        <div className="review" key={idx}>

                                            <div>
                                                {reviewArr(review.rating).map((_, starIdx) => (
                                                    <img
                                                        key={starIdx}
                                                        src={star}
                                                        alt="star"
                                                    />
                                                ))}
                                            </div>

                                            <div>
                                                <h5>{review.comment}</h5>
                                                <h6>{review.reviewer}</h6>
                                            </div>

                                        </div>

                                    ))

                                ) : (
                                    <div className="empty-slots">
                                        У этого агента пока нет отзывов
                                    </div>
                                )}

                            </div>

                        ))}

                    </div>

                </div>

            </div>

        </div>
    );
}

export default Agents;