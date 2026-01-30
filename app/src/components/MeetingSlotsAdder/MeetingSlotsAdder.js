import { useAuth } from '../AuthContext';
import './MeetingSlotsAdder.css';
import { useState } from 'react';
import remove from './remove.png';
import calendar from './calendar.png';

function MeetingSlotsAdder() {
    const { authenticatedUser } = useAuth();
    const [slots, setSlots] = useState([]);
    const [date, setDate] = useState(new Date().toISOString().slice(0, 16));
    const [isSuccess, setIsSuccess] = useState(false);
    const [isFailed, setIsFailed] = useState(false);

    const handleRemoveSlot = (index) => {
        setSlots(prev => prev.filter((_, idx) => idx != index));
    }

    const handleAddToCalendar = (e) => {
        e.preventDefault();

        const body = {
            slots: slots
        };

        const add = async () => {
            const response = await fetch('/api/agent/add-meeting-slots', {
                method: "POST",
                headers: {
                    "Authorization": "Bearer " + authenticatedUser.token,
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(body)
            });

            if (!response.ok) {
                console.log("Failed to add dates to calendar");
                setIsSuccess(false);
                setIsFailed(true);
                return;
            }

            setIsFailed(false);
            setIsSuccess(true);
        };

        add();
    }

    return (
        <div className='meeting-slots'>
            <h1 className='lead'>Добро пожаловать, Агент!</h1>
            <p>Здесь вы можете удобно указать доступное время для встреч с клиентами.
                Просто укажите дату, время и любые дополнительные детали, чтобы обеспечить бесперебойное планирование.
            </p>
            <hr></hr>
            <div className='info'>
                <img src={calendar} width='100' height='100'/>
                <div className='adder'>
                    <input type='datetime-local' value={date} onChange={(e) => setDate(e.target.value)}></input>
                    <button className='btn btn-dark' onClick={() => setSlots((prevSlots) => [...prevSlots, date])}>Добавить</button>
                </div>
            </div>

            {slots.length > 0 ? (
                <div className='slots'>
                    {slots.map((slot, idx) => (
                        <div className='date'>
                            {new Date(slot).toLocaleString().slice(0, -3)}
                            <img src={remove} width='30' height='30' onClick={() => handleRemoveSlot(idx)}/>
                        </div>
                    ))}
                    <hr></hr>
                    <h6>Теперь вы можете добавить выбранные посещения в свой календарь.</h6>
                    <button className='btn btn-dark' onClick={handleAddToCalendar}>Добавить в календарь</button>
                    {isSuccess && (
                        <div className='alert alert-success'>
                            Свободные слоты успешно добавлены в календарь.
                        </div>
                    )}
                    {isFailed && (
                        <div className='alert alert-danger'>
                            Не удалось добавить доступные слоты в календарь.
                        </div>
                    )}
                </div>
            ) : (
                <div></div>
            )}
        </div>
    )
}

export default MeetingSlotsAdder;