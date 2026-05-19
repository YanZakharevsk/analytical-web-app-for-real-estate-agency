import { useState } from 'react';
import { useAuth } from '../AuthContext';
import './AskQuestion.css';

function AskQuestion() {
    const { authenticatedUser } = useAuth();
    const [question, setQuestion] = useState('');
    const [error, setError] = useState('');

    const submitQuestion = () => {
        setError('');

        if (!authenticatedUser?.token) {
            setError('Войдите в систему, чтобы отправить вопрос.');
            return;
        }

        const text = question.trim();

        if (!text) {
            setError('Введите текст вопроса.');
            return;
        }

        setError('Отправка вопросов с этой страницы пока не подключена к API.');
    };

    return (
        <div className="re-page re-page--transparent">

            <section className="re-hero">
                <h2>Вопрос</h2>
                <p>Свяжитесь с нами по объекту.</p>
            </section>

            <div className="re-inner re-inner--narrow">

                <div className="re-surface ask-question-wrap">

                    <div className='ask-question'>

                        <h2>Появился вопрос?</h2>

                        <p>
                            Не стесняйтесь задавать любые вопросы
                            о наших услугах, объектах недвижимости
                            или процессе покупки и аренды жилья.
                            Команда Real Estate всегда готова помочь.
                        </p>

                        <hr />

                        <textarea
                            placeholder='Введите ваш вопрос...'
                            value={question}
                            onChange={(e) => setQuestion(e.target.value)}
                        />

                        {error && (
                            <p className="ask-question-error">
                                {error}
                            </p>
                        )}

                        <button
                            disabled={question.trim().length === 0}
                            onClick={submitQuestion}
                        >
                            Отправить вопрос
                        </button>

                    </div>

                </div>

            </div>

        </div>
    );
}

export default AskQuestion;