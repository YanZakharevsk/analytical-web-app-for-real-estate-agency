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
        <div className="re-page">
            <section className="re-hero">
                <h2>Вопрос</h2>
                <p>Свяжитесь с нами по объекту.</p>
            </section>
            <div className="re-inner re-inner--narrow">
            <div className="re-surface ask-question-wrap">
        <div className='ask-question'>
            <h2>Появился вопрос?</h2>
            <p>Не стесняйтесь задавать нам любые вопросы о наших услугах, объектах недвижимости или о чем-либо еще, связанном с недвижимостью!</p>
            <hr></hr>
            <input
                type='text'
                placeholder='Задавайте вопрос...'
                value={question}
                onChange={(e) => setQuestion(e.target.value)}
            />
            {error && <p className="ask-question-error">{error}</p>}
            <button className='btn btn-dark' disabled={question.trim().length === 0} onClick={submitQuestion}>
                Подтвердить
            </button>
        </div>
            </div>
            </div>
        </div>
    );
}

export default AskQuestion;