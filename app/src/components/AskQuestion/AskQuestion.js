import { useState } from 'react';
import { useAuth } from '../AuthContext';
import './AskQuestion.css';
import { useParams } from 'react-router-dom';

function AskQuestion() {
    const { authenticatedUser } = useAuth();
    const [question, setQuestion] = useState("");
    const { id } = useParams();

    const submitQuestion = () => {
        if (authenticatedUser.token === '') {
            alert("You have to sign in first");
            return;
        };

        // const submit = async () => {
        //     // TO-DO
        //     const response = await fetch(`/api/`, {
        //         method: "POST",
        //     });

        // }

    };

    return (
        <div className='ask-question'>
            <h2>Появился вопрос?</h2>
            <p>Не стесняйтесь задавать нам любые вопросы о наших услугах, объектах недвижимости или о чем-либо еще, связанном с недвижимостью!</p>
            <hr></hr>
            <input type='text' placeholder='Задавайте вопрос...' onChange={(e) => setQuestion(e.target.value)}></input>
            <button className='btn btn-dark' disabled={question.length === 0}>Подтвердить</button>
        </div>
    );
}

export default AskQuestion;