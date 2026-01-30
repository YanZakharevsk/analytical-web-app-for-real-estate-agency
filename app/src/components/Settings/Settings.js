import './Settings.css';
import { useEffect, useState } from 'react';
import { useAuth } from '../AuthContext';
import user from './user.png';
import padlock from './padlock.png';
import pass from './pass.png';

function Settings() {
    const { authenticatedUser } = useAuth();
    const [details, setDetails] = useState(null);
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [email, setEmail] = useState("");
    const [phone, setPhone] = useState("");
    const [username, setUsername] = useState("");

    const [currentPassword, setCurrentPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [repeatPassword, setRepeatPassword] = useState("");

    const [isSuccess, setIsSuccess] = useState(false);
    const [isFailed, setIsFailed] = useState({
        status: false,
        info: ""
    });

    const isAnyCredentialFilled = () => {
        return firstName !== "" || lastName !== "" || email !== "" || phone !== "" || username !== "";
    }

    const arePasswordsFilled = () => {
        return currentPassword !== "" && newPassword !== "" && repeatPassword != "";
    }

    useEffect(() => {
        const fetchDetails = async () => {
            const response = await fetch('/api/user-details', {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + authenticatedUser.token
                }
            });

            if (!response.ok) {
                console.log("Failed to fetch details");
                return;
            };

            const data = await response.json();
            setDetails(data);
        };

        fetchDetails();

    }, [authenticatedUser, isSuccess]);

    const onCredentialsSubmit = (e) => {
        e.preventDefault();

        const updateCredentials = async () => {
            const body = {
                firstName: firstName,
                lastName: lastName,
                username: username,
                phoneNumber: phone,
                email: email
            };

            const response = await fetch('/api/update-credentials', {
                method: "PATCH",
                headers: {
                    "Authorization": "Bearer " + authenticatedUser.token,
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(body)
            });

            if (!response.ok) {
                console.log("Failed to update credentials");

                setIsFailed({
                    status: true,
                    info: response
                });

                setIsSuccess(false);

                return;
            }

            setIsSuccess(true);
            setIsFailed(false);
            setDetails(body);
        };

        updateCredentials();
    };

    const onPasswordSubmit = (e) => {
        e.preventDefault();

        if (newPassword !== repeatPassword) {
            setIsFailed({
                status: true,
                info: "Provided passwords don't match"
            });

            return;
        }

        const updatePasswords = async () => {
            const body = {
                oldPassword: currentPassword,
                newPassword: newPassword
            };

            const response = await fetch('/api/update-password', {
                method: "PATCH",
                headers: {
                    "Authorization": "Bearer " + authenticatedUser.token,
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(body)
            });

            if (!response.ok) {
                console.log("Failed to update passwords");

                setIsFailed({
                    status: true,
                    info: response
                });

                setIsSuccess(false);

                return;
            };

            setIsSuccess(true);
            setIsFailed(false);
        };

        updatePasswords();
    };

    return (
        <div className='settings'>
            <h4>Управление своей учетной записью</h4>
            <hr></hr>
            <div className='account-info'>
                <img src={user}/>
                <div className='info'>
                    {details !== null && <h3>{details.firstName + " " + details.lastName}</h3>}
                    {details !== null && <p>{details.email}</p>}
                    {details !== null && <p>{details.phoneNumber}</p>}
                </div>
            </div>
            <div className='edit-credentials'>
                <div className='credentials'>
                    <div className='header'>
                        <h5>
                            Обновить учетные данные</h5>
                        <img src={pass}/>
                    </div>
                    <div>
                        <label htmlFor='firstName'>Имя</label>
                        <input type='text' id='firstName' onChange={(e) => setFirstName(e.target.value)}></input>
                    </div>
                    <div>
                        <label htmlFor='lastName'>Фамилия</label>
                        <input type='text' id='lastName' onChange={(e) => setLastName(e.target.value)}></input>
                    </div>
                    <div>
                        <label htmlFor='username'>Логин</label>
                        <input type='text' id='username' onChange={(e) => setUsername(e.target.value)}></input>
                    </div>
                    <div>
                        <label htmlFor='email'>Email</label>
                        <input type='email' id='email' onChange={(e) => setEmail(e.target.value)}></input>
                    </div>
                    <div>
                        <label htmlFor='phone'>Номер телефона</label>
                        <input type='phone' onChange={(e) => setPhone(e.target.value)} pattern="[0-9]{3} [0-9]{3}, [0-9]{3}"></input>
                    </div>
                    <button className='btn btn-dark' disabled={!isAnyCredentialFilled()} onClick={onCredentialsSubmit}>Обновить</button>
                </div>
                <div className='password'>
                    <div className='header'>
                        <h5>Обновить пароль</h5>
                        <img src={padlock}/>
                    </div>
                    <div>
                        <label htmlFor='oldPassword'>Текущий пароль</label>
                        <input type='password' id='oldPassword' onChange={(e) => setCurrentPassword(e.target.value)}></input>
                    </div>
                    <div>
                        <label htmlFor='newPassword'>Новый пароль</label>
                        <input type='password' id='newPassword' onChange={(e) => setNewPassword(e.target.value)}></input>
                    </div>
                    <div>
                        <label htmlFor='repeatPassword'>Повторите пароль</label>
                        <input type='password' id='repeatPassword' onChange={(e) => setRepeatPassword(e.target.value)}></input>
                    </div>
                    <button className='btn btn-dark' disabled={!arePasswordsFilled()} onClick={onPasswordSubmit}>Сбросить пароль</button>
                </div>
                {isSuccess && (
                    <div className='alert alert-success'>
                        Успешное обновление пользовательской информации
                    </div>
                )}
                {isFailed.status && (
                    <div className='alert alert-danger'>
                        Не удалось обновить информацию о пользователе: {isFailed.info}
                    </div>
                )}
            </div>
        </div>
    );
}

export default Settings;