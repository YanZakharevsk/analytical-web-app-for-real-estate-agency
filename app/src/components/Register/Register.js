import { useState } from 'react';
import './Register.css';

function Register() {
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [repeatPassword, setRepeatPassword] = useState("");
    const [email, setEmail] = useState("");
    const [phoneNumber, setPhoneNumber] = useState("");
    const [passwordMatch, setPasswordMatch] = useState(true);
    const [role, setRole] = useState("");
    const [registrationFailure, setRegistrationFailure] = useState(false);
    const [registrationSuccess, setRegistrationSuccess] = useState(false);

    const isAnyFieldEmpty = () => {
        return !(
            firstName &&
            lastName &&
            username &&
            password &&
            repeatPassword &&
            email &&
            phoneNumber &&
            role
        );
    };

    const handleClear = (e) => {
        e.preventDefault();

        setFirstName("");
        setLastName("");
        setUsername("");
        setPassword("");
        setRepeatPassword("");
        setEmail("");
        setPhoneNumber("");
        setRole("");
        setPasswordMatch(true); 
    }

    const handlePhoneChange = (e) => {
        let value = e.target.value.replace(/\D/g, "");

        if (!value.startsWith("375")) {
            value = "375" + value;
        }

        // Ограничиваем длину (375 + 9 цифр = 12)
        value = value.slice(0, 12);

        // Создаём форматированное отображение
        let formatted = "+375";

        if (value.length > 3) {
            const code = value.slice(3, 5); // оператор: 29 / 44 / 33
            formatted += ` (${code})`;
        }

        if (value.length > 5) {
            formatted += " " + value.slice(5, 8);
        }

        if (value.length > 8) {
            formatted += "-" + value.slice(8, 10);
        }

        if (value.length > 10) {
            formatted += "-" + value.slice(10, 12);
        }

        setPhoneNumber(formatted);
    };


    const handleSubmit = (e) => {
        e.preventDefault();

        if (password !== repeatPassword) {
            setPasswordMatch(false);
            return;
        }

        const cleanedPhone = phoneNumber.replace(/\D/g, "");

        const body = {
            firstName,
            lastName,
            username,
            password,
            email,
            phoneNumber: "+" + cleanedPhone // +375447039635
        };

        const register = async () => {
            const response = await fetch(`/api/auth/${role}/register`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                }, 
                body: JSON.stringify(body)
            });

            if (!response.ok) {
                console.log("Failed to register");
                setRegistrationFailure(true);
                return;
            }

            setRegistrationSuccess(true);
        }



        register();
    }

    return (
        <div className='registration'>
            <h2 className='lead'>Зарегистрируйтесь для полного доступа</h2>
            <hr></hr>
            <form>
                <label htmlFor='fName'>Имя</label>
                <input type='text' id='fName' name='fName' value={firstName} onChange={(e) => setFirstName(e.target.value)}></input>
                <label htmlFor='lName'>Фамилия</label>
                <input type='text' id='lName' name='lName' value={lastName} onChange={(e) => setLastName(e.target.value)}></input>
                <label htmlFor='username'>Логин</label>
                <input type='text' id='username' name='username' value={username} onChange={(e) => setUsername(e.target.value)}></input>
                <label htmlFor='fPassword'>Пароль</label>
                <input type='password' id='fPassword' name='fPassword' value={password} onChange={(e) => setPassword(e.target.value)}></input>
                <label htmlFor='rPassword'>Повторите пароль</label>
                <input type='password' id='rPassword' name='rPassword' value={repeatPassword} onChange={(e) => setRepeatPassword(e.target.value)}></input>
                {!passwordMatch && (
                    <div class="alert alert-danger" role="alert">
                        <p>You successfully signed in.</p>
                    </div>
                )}
                <label htmlFor='email'>Email</label>
                <input type='email' id='email' name='email' value={email} onChange={(e) => setEmail(e.target.value)}></input>
                <label htmlFor='phoneNumber'>Номер телефона</label>
                <input
                    type="tel"
                    id="phoneNumber"
                    name="phoneNumber"
                    value={phoneNumber}
                    onChange={handlePhoneChange}
                />
                <label>Ваша роль</label>
                <div className='checkboxes'>
                    <div>
                        <input className="form-check-input" type="radio" name="role" id="customer" value="customer" onChange={(e) => setRole(e.target.value)}/>
                        <label className="form-check-label" for="customer">Клиент</label>
                    </div>
                    <div>
                        <input className="form-check-input" type="radio" name="role" id="owner" value="owner" onChange={(e) => setRole(e.target.value)}/>
                        <label className="form-check-label" for="owner">Владелец</label>
                    </div>
                </div>
                <div className='buttons'>
                    <button className='btn btn-dark' onClick={handleSubmit} disabled={isAnyFieldEmpty()}>Подтвердить</button>
                    <button className='btn btn-dark' onClick={handleClear}>Очистить форму</button>
                </div>
                {registrationSuccess && (
                    <div class="alert alert-success" role="alert">
                        <p>Успешное создание аккаунта</p>
                    </div>
                )}
                {registrationFailure && (
                    <div class="alert alert-danger" role="alert">
                        <p>Не удалось создать учетную запись.</p>
                    </div>
                )}
            </form>

        </div>
    );
}

export default Register;