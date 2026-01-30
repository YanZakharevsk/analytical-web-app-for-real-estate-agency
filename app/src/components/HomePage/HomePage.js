import './HomePage.css';
import logo from '../logo2.jpg';

function HomePage() {
    return (
        <div className="homepage">
            {/* Hero Section - на всю ширину */}
            <section className="hero">
                <div className="hero-content">
                    <h1>Добро пожаловать в Real Estate!</h1>
                    <p className="hero-subtitle">
                        Ваш надёжный партнёр в мире недвижимости. Находим дома, которые становятся историями.
                    </p>
                </div>
            </section>

            {/* Logo & Intro Section */}
            <section className="logo-section">
                <img src={logo} alt="Real Estate Logo" />
                <div className="logo-text">
                    <h4>Премиальное риэлторское агентство</h4>
                    <p>
                        Более <strong>15 лет</strong> помогаем клиентам находить идеальное жильё и совершать
                        выгодные сделки на рынке недвижимости. Наш профессиональный подход и внимание
                        к деталям делают процесс покупки или продажи недвижимости простым и приятным.
                    </p>
                </div>
            </section>

            {/* About Section */}
            <section className="about-section">
                <div className="about-card">
                    <h4>О нас</h4>
                    <p>
                        В риэлторском агентстве <strong>Real Estate</strong> мы верим в преобразующую силу поиска
                        идеального дома. Основываясь на принципах честности, профессионализма и
                        индивидуального подхода, мы стремимся помочь нашим клиентам уверенно и
                        спокойно ориентироваться в сложном мире недвижимости.
                    </p>
                </div>

                <div className="about-card">
                    <h4>Наша миссия</h4>
                    <p>
                        Наша миссия в Real Estate проста, но значима: предоставлять непревзойдённый
                        сервис и сопровождение на каждом этапе вашего пути в сфере недвижимости.
                        Независимо от того, покупаете вы, продаёте или арендуете жильё, наша команда
                        профессионалов сделает всё возможное, чтобы ваш опыт был не только успешным,
                        но и приятным.
                    </p>
                </div>
            </section>

            {/* Statistics Section */}
            <section className="stats-section">
                <div className="stats-grid">
                    <div className="stat-item">
                        <span className="stat-number">15+</span>
                        <span className="stat-label">Лет опыта</span>
                    </div>
                    <div className="stat-item">
                        <span className="stat-number">500+</span>
                        <span className="stat-label">Успешных сделок</span>
                    </div>
                    <div className="stat-item">
                        <span className="stat-number">98%</span>
                        <span className="stat-label">Довольных клиентов</span>
                    </div>
                    <div className="stat-item">
                        <span className="stat-number">24/7</span>
                        <span className="stat-label">Поддержка</span>
                    </div>
                </div>
            </section>

            {/* Features Section */}
            <section className="features-section">
                <div className="features-container">
                    <h2>Почему выбирают Real Estate?</h2>
                    <div className="features-grid">
                        <div className="feature-card">
                            <h5>Экспертность и опыт</h5>
                            <hr />
                            <p>
                                Опираясь на многолетний опыт и глубокое понимание локального рынка,
                                наша команда специалистов обладает знаниями, навыками и аналитическими
                                инструментами для того, чтобы помочь вам принимать взвешенные решения.
                                Ищете ли вы дом мечты, ведёте переговоры о выгодной сделке или хотите
                                эффективно продвинуть свою недвижимость — Real Estate обеспечит отличный результат.
                            </p>
                        </div>

                        <div className="feature-card">
                            <h5>Ориентация на клиента</h5>
                            <hr />
                            <p>
                                В Real Estate клиент всегда на первом месте. Мы стремимся выстраивать
                                доверительные отношения, основанные на уважении и открытой коммуникации.
                                От первого обращения до финального этапа сделки мы обеспечиваем
                                персональное внимание, экспертное сопровождение и поддержку на каждом шаге.
                            </p>
                        </div>

                        <div className="feature-card">
                            <h5>Участие в жизни сообщества</h5>
                            <hr />
                            <p>
                                Будучи частью местного сообщества, мы гордимся возможностью вносить вклад
                                в развитие района. Через благотворительные инициативы, волонтёрскую деятельность
                                и поддержку локального бизнеса Real Estate стремится укреплять атмосферу
                                единства и благополучия в наших округах.
                            </p>
                        </div>
                    </div>
                </div>
            </section>
        </div>
    );
}

export default HomePage;