import { useState } from 'react';
import { useAuth } from '../AuthContext';
import './EstimatePage.css';
import image from './images (1).jpg';
import { trackEvent } from '../../utils/analyticsTrack';


const estateTypes = [
    { value: 'APARTMENT', label: 'Квартира' },
    { value: 'BUNGALOW', label: 'Частный дом' },
    { value: 'COTTAGE', label: 'Коттедж' },
    { value: 'MANSION', label: 'Особняк' }
];

const availabilityTypes = [
    { value: 'FOR_SALE', label: 'Продажа' },
    { value: 'FOR_RENT', label: 'Аренда' }
];

const conditions = [
    { value: 'NEEDS_RENOVATION', label: 'Требует ремонта' },
    { value: 'DEVELOPER_CONDITION', label: 'От застройщика' },
    { value: 'AFTER_RENOVATION', label: 'После ремонта' },
    { value: 'NORMAL_USE_SIGNS', label: 'В хорошем состоянии' }
];

function EstimatePage() {
    const { authenticatedUser } = useAuth();
    const [step, setStep] = useState(1);
    const [form, setForm] = useState({
        estateType: '',
        availability: '',
        area: '',
        rooms: '',
        floor: '',
        totalFloors: '',
        condition: ''
    });
    const [result, setResult] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const nextDisabled =
        (step === 1 && (!form.estateType || !form.availability)) ||
        (step === 2 && (!form.area || !form.rooms || (!form.floor && !form.totalFloors))) ||
        (step === 3 && !form.condition);

    // --- Функция для отправки данных на бэк ---
    const handleEstimate = async () => {
        setLoading(true);
        setError(null);
        trackEvent({
            eventName: "price_estimation_calculate",
            category: "INTERACTION",
            properties: {
                property_type: form.estateType,
                area: Number(form.area),
                rooms_count: Number(form.rooms),
                location: "unknown"
            }
        }, authenticatedUser?.token);

        if (!authenticatedUser?.token) {
            setError('Для расчёта необходима авторизация');
            setLoading(false);
            return;
        }

        const payload = {
            estateType: form.estateType,
            availability: form.availability,
            area: Number(form.area),
            rooms: Number(form.rooms),
            floor: form.floor ? Number(form.floor) : null,
            totalFloors: form.totalFloors ? Number(form.totalFloors) : null,
            condition: form.condition
        };

        try {
            const response = await fetch('/api/estimate', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + authenticatedUser.token
                },
                body: JSON.stringify(payload)
            });

            if (!response.ok) {
                throw new Error('Ошибка при расчёте. Попробуйте снова.');
            }

            const data = await response.json();
            setResult(data);
            if (data?.found) {
                trackEvent({
                    eventName: "price_estimation_completed",
                    category: "CONVERSION",
                    properties: {
                        estimation_id: `${Date.now()}`,
                        property_type: form.estateType,
                        calculated_price: data.estimatedPrice
                    }
                }, authenticatedUser?.token);
            }
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };



    return (
        <div className="estimate-page">
            <section className="estimate-hero">
                <div className="estimate-left">
                    <h1>Узнайте стоимость недвижимости бесплатно</h1>
                    <p className="subtitle">
                        Сервис оценки недвижимости от <strong>Real Estate</strong>
                    </p>

                    <div className="steps">
                        <span className={step === 1 ? 'active' : ''}>1</span>
                        <span className={step === 2 ? 'active' : ''}>2</span>
                        <span className={step === 3 ? 'active' : ''}>3</span>
                    </div>

                    {step === 1 && (
                        <div className="step-block">
                            <h3>Тип недвижимости</h3>
                            <select value={form.estateType} onChange={e => setForm({ ...form, estateType: e.target.value })}>
                                <option value="">Выберите тип</option>
                                {estateTypes.map(t => <option key={t.value} value={t.value}>{t.label}</option>)}
                            </select>
                            <select value={form.availability} onChange={e => setForm({ ...form, availability: e.target.value })}>
                                <option value="">Продажа / аренда</option>
                                {availabilityTypes.map(a => <option key={a.value} value={a.value}>{a.label}</option>)}
                            </select>
                        </div>
                    )}

                    {step === 2 && (
                        <div className="step-block">
                            <h3>Характеристики</h3>
                            <input type="number" placeholder="Площадь, м²" value={form.area} onChange={e => setForm({ ...form, area: e.target.value })} />
                            <input type="number" placeholder="Количество комнат" value={form.rooms} onChange={e => setForm({ ...form, rooms: e.target.value })} />
                            {form.estateType === 'APARTMENT' ? (
                                <input type="number" placeholder="Этаж" value={form.floor} onChange={e => setForm({ ...form, floor: e.target.value })} />
                            ) : (
                                <input type="number" placeholder="Количество этажей" value={form.totalFloors} onChange={e => setForm({ ...form, totalFloors: e.target.value })} />
                            )}
                        </div>
                    )}

                    {step === 3 && (
                        <div className="step-block">
                            <h3>Состояние</h3>
                            <div className="condition-buttons">
                                {conditions.map(c => (
                                    <button key={c.value} className={form.condition === c.value ? 'active' : ''} onClick={() => setForm({ ...form, condition: c.value })}>
                                        {c.label}
                                    </button>
                                ))}
                            </div>
                        </div>
                    )}

                    <div className="step-actions">
                        {step > 1 && <button className="back" onClick={() => setStep(step - 1)}>Назад</button>}
                        {step < 3 ? (
                            <button className="next" disabled={nextDisabled} onClick={() => setStep(step + 1)}>Далее</button>
                        ) : (
                            <button
                                className="next"
                                disabled={nextDisabled || loading}
                                onClick={() => step < 3 ? setStep(step + 1) : handleEstimate()}
                            >
                                {step < 3 ? 'Далее' : loading ? 'Считаем...' : 'Оценить'}
                            </button>
                        )}
                    </div>

                    {error && <p className="estimate-error">{error}</p>}

                    {result && (
                        <div className="estimate-result">
                            {result.found ? (
                                <>
                                    <h3 style={{ color: '#2196f3' }}>
                                        Примерная стоимость: {result.estimatedPrice.toLocaleString('ru-RU', { style: 'currency', currency: 'USD' })}
                                    </h3>
                                    <p>
                                        Доля аналогов на рынке:{' '}
                                        <span style={{ color: result.percent < 10 ? 'red' : 'green', fontWeight: '600' }}>
                        {result.percent.toFixed(1)}%
                    </span>
                                    </p>
                                    <p style={{ color: result.percent < 10 ? 'red' : 'green', fontWeight: '500' }}>
                                        {result.percent < 10
                                            ? 'Аналогов мало, поэтому планируйте цену аккуратно'
                                            : 'Рынок насыщен аналогами — ориентируйтесь на предложенную цену'}
                                    </p>
                                </>
                            ) : (
                                <p>{result.message}</p>
                            )}
                        </div>
                    )}



                </div>

                <div className="estimate-right">
                    <img src={image} alt="Оценка недвижимости" />
                </div>
            </section>

            {/* ================= 3 КАРТОЧКИ ================= */}
            {/* ================= КАК ЭТО РАБОТАЕТ ================= */}
            <section className="estimate-how">
                <h2>Как это работает?</h2>

                <div className="how-cards">
                    <div className="how-card">
                        <div className="how-icon">📊</div>
                        <h4>Сравнение с аналогами</h4>
                        <p>
                            Подбираем и анализируем похожие предложения на рынке недвижимости.
                        </p>
                    </div>

                    <div className="how-card">
                        <div className="how-icon">⚙️</div>
                        <h4>Учет особенностей</h4>
                        <p>
                            Корректируем стоимость исходя из площади, ремонта и других параметров имущества.
                        </p>
                    </div>

                    <div className="how-card">
                        <div className="how-icon">📈</div>
                        <h4>Рыночная корректировка</h4>
                        <p>
                            Рассчитываем итоговую цену с учетом актуальных трендов и конъюнктуры рынка.
                        </p>
                    </div>
                </div>
            </section>


            {/* ================= FAQ ================= */}
            <section className="estimate-faq">
                <h2>Часто задаваемые вопросы</h2>

                <details>
                    <summary>Для чего нужна оценка?</summary>
                    <p>
                        <strong>Продавцам</strong> — помогает определить реальную рыночную стоимость недвижимости
                        перед размещением объявления.<br/><br/>
                        <strong>Покупателям</strong> — служит аргументом при переговорах и торге с продавцом.
                    </p>
                </details>

                <details>
                    <summary>Как рассчитывается стоимость недвижимости?</summary>
                    <p>
                        Мы проанализировали множество объявлений и совершённых сделок и выделили ключевые факторы,
                        влияющие на стоимость недвижимости.
                        <br/><br/>
                        На основе этих факторов система <strong>Real Estate Оценка</strong> сопоставляет ваш объект
                        с аналогичными предложениями и реальными сделками, учитывая:
                        активность спроса, количество объектов в продаже, состояние рынка и другие параметры.
                    </p>
                </details>

                <details>
                    <summary>Насколько точна ваша оценка?</summary>
                    <p>
                        Сервис стремится предоставить максимально объективную рыночную стоимость,
                        основываясь на наиболее значимых параметрах объекта.
                        <br/><br/>
                        В отдельных случаях может потребоваться участие специалиста для более детального анализа —
                        например, с учётом качества ремонта, наличия мебели, техники,
                        дизайнерских решений или перепланировки.
                    </p>
                </details>
            </section>

        </div>
    );
}

export default EstimatePage;
