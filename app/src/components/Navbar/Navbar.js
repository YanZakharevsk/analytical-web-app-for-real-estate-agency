import { Link } from 'react-router-dom';
import { useAuth } from "../AuthContext";
import './Navbar.css';
import logo from './logo.png'

function Navbar() {
    const { authenticatedUser } = useAuth();
    return (
        <div className='navbar'>
            <ul>
                <li><Link to="/homepage">Главная</Link></li>
                <li><Link to="/agents">Агенты</Link></li>
                <li><Link to="/estates">Недвижимость</Link></li>
                <li><Link to="/analytics">Аналитика</Link></li>
            </ul>
            <div>
                <img src={logo} alt='logo' width={100} height={50}></img>
            </div>
            <ul>

                <li><Link to="/register">Регистрация</Link></li>
                <li><Link to="/login">Войти</Link></li>
                <li><Link to="/account" id='account' className='disabled-link'>Аккаунт</Link></li>
            </ul>
        </div>
    )
}

export default Navbar;