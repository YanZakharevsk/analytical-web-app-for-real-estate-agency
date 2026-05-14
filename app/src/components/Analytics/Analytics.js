import { useState, useEffect, useRef } from 'react';
import { useAuth } from '../AuthContext.js';
import { Link } from 'react-router-dom';
import { trackBatch } from '../../utils/analyticsTrack';
import './Analytics.css';
import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    Tooltip,
    Legend,
    ResponsiveContainer,
    PieChart,
    Pie,
    Cell,
    Area
} from 'recharts';

function Analytics() {

    const { authenticatedUser } = useAuth();

    const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#A28FFF', '#FF6699'];


    // ===== ГРАФИК 1: динамика цен =====
    const [segment, setSegment] = useState('ALL');
    const [rooms, setRooms] = useState('');
    const [priceIndex, setPriceIndex] = useState('TOTAL');
    const [priceDynamicsData, setPriceDynamicsData] = useState([]);

    // ===== ГРАФИК 2: индекс квартир =====
    const [apartmentIndexData, setApartmentIndexData] = useState([]);
    const [apartmentSummary, setApartmentSummary] = useState(null);

    // ===== ГРАФИК 3: аренда =====
    const [rentIndexData, setRentIndexData] = useState([]);
    const [rentSummary, setRentSummary] = useState(null);

    //КРУГОВЫЕ
    const [agentOffersData, setAgentOffersData] = useState([]);
    const [agentArchivedData, setAgentArchivedData] = useState([]);

    const prevSegment = useRef(segment);
    const prevPriceIndex = useRef(priceIndex);

    useEffect(() => {
        if (prevSegment.current === segment) {
            return;
        }
        trackBatch([{
            eventName: 'analytics_segment_select',
            category: 'INTERACTION',
            properties: { segment_type: segment, previous_segment: prevSegment.current }
        }], authenticatedUser?.token);
        prevSegment.current = segment;
    }, [segment, authenticatedUser?.token]);

    useEffect(() => {
        if (prevPriceIndex.current === priceIndex) {
            return;
        }
        trackBatch([{
            eventName: 'analytics_index_select',
            category: 'INTERACTION',
            properties: { index_type: priceIndex, previous_index: prevPriceIndex.current }
        }], authenticatedUser?.token);
        prevPriceIndex.current = priceIndex;
    }, [priceIndex, authenticatedUser?.token]);


    // ===== FETCH 1 =====
    useEffect(() => {
        if (!authenticatedUser || !authenticatedUser.token) return;

        fetch('/api/analytics/price-dynamics', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + authenticatedUser.token
            },
            body: JSON.stringify({
                segment: segment,
                rooms: segment === 'ROOMS' && rooms !== '' ? Number(rooms) : null,
                priceIndex: priceIndex
            })
        })
            .then(res => res.json())
            .then(data => {
                setPriceDynamicsData(data);
                trackBatch([{
                    eventName: 'analytics_data_loaded',
                    category: 'SYSTEM',
                    properties: {
                        segment_type: segment,
                        index_type: priceIndex,
                        records_count: Array.isArray(data) ? data.length : 0
                    }
                }], authenticatedUser?.token);
                if (Array.isArray(data) && data.length > 0) {
                    trackBatch([{
                        eventName: 'analytics_report_generated',
                        category: 'CONVERSION',
                        properties: {
                            segment_type: segment,
                            index_type: priceIndex,
                            records_count: data.length
                        }
                    }], authenticatedUser?.token);
                }
            })
            .catch(() => {
                trackBatch([{
                    eventName: 'analytics_data_error',
                    category: 'SYSTEM',
                    properties: { error_code: 'price_dynamics_fetch_failed', segment_type: segment }
                }], authenticatedUser?.token);
                console.log('Failed to fetch price dynamics');
            });

    }, [segment, rooms, priceIndex, authenticatedUser]);

    // ===== FETCH 2 =====
    useEffect(() => {
        if (!authenticatedUser || !authenticatedUser.token) return;

        fetch('/api/analytics/apartment-price-index', {
            headers: {
                'Authorization': 'Bearer ' + authenticatedUser.token
            }
        })
            .then(res => res.json())
            .then(data => {
                setApartmentIndexData(data.points);
                setApartmentSummary(data.summary);
                trackBatch([{
                    eventName: 'analytics_data_loaded',
                    category: 'SYSTEM',
                    properties: {
                        segment_type: 'APARTMENT_PRICE_INDEX',
                        index_type: 'PER_M2',
                        records_count: Array.isArray(data.points) ? data.points.length : 0
                    }
                }], authenticatedUser?.token);
            })
            .catch(() => {
                trackBatch([{
                    eventName: 'analytics_data_error',
                    category: 'SYSTEM',
                    properties: { error_code: 'apartment_index_fetch_failed', segment_type: 'APARTMENT_PRICE_INDEX' }
                }], authenticatedUser?.token);
                console.log('Failed to fetch apartment index');
            });

    }, [authenticatedUser]);

    // ===== FETCH 3 =====
    useEffect(() => {
        if (!authenticatedUser || !authenticatedUser.token) return;

        fetch('/api/analytics/rent-price-index', {
            headers: {
                'Authorization': 'Bearer ' + authenticatedUser.token,
            }
        })
            .then(res => res.json())
            .then(data => {
                setRentIndexData(data.points);
                setRentSummary(data.summary);
                trackBatch([{
                    eventName: 'analytics_data_loaded',
                    category: 'SYSTEM',
                    properties: {
                        segment_type: 'RENT_PRICE_INDEX',
                        index_type: 'TOTAL',
                        records_count: Array.isArray(data.points) ? data.points.length : 0
                    }
                }], authenticatedUser?.token);
            })
            .catch(() => {
                trackBatch([{
                    eventName: 'analytics_data_error',
                    category: 'SYSTEM',
                    properties: { error_code: 'rent_index_fetch_failed', segment_type: 'RENT_PRICE_INDEX' }
                }], authenticatedUser?.token);
                console.log('Failed to fetch rent index');
            });

    }, [authenticatedUser]);

    // FETCH 4
    useEffect(() => {
        if (!authenticatedUser?.token) return;

        fetch('/api/analytics/agents', {
            headers: {
                Authorization: 'Bearer ' + authenticatedUser.token
            }
        })
            .then(res => res.json())
            .then(data => {
                setAgentOffersData(data.offers || []);
                setAgentArchivedData(data.archived || []);
                trackBatch([{
                    eventName: 'analytics_data_loaded',
                    category: 'SYSTEM',
                    properties: {
                        segment_type: 'AGENT_STATS',
                        index_type: 'SHARE',
                        records_count: (data.offers || []).length + (data.archived || []).length
                    }
                }], authenticatedUser?.token);
            })
            .catch(() => {
                trackBatch([{
                    eventName: 'analytics_data_error',
                    category: 'SYSTEM',
                    properties: { error_code: 'agent_stats_fetch_failed', segment_type: 'AGENT_STATS' }
                }], authenticatedUser?.token);
                console.log('Failed to fetch agent stats');
            });
    }, [authenticatedUser]);




    return (
        <div className="analytics">

            <h2>Аналитика рынка недвижимости</h2>
            <hr/>

            {/* ================= ГРАФИК 1 ================= */}
            <section className="analytics-block">
                <h3>Динамика цен на недвижимость в Беларуси</h3>

                <div className="controls">
                    <span>Сегмент:</span>
                    <button className={segment === 'ALL' ? 'active' : ''} onClick={() => setSegment('ALL')}>
                        В целом
                    </button>
                    <button className={segment === 'ROOMS' ? 'active' : ''} onClick={() => setSegment('ROOMS')}>
                        По комнатам
                    </button>

                    {segment === 'ROOMS' && (
                        <select value={rooms} onChange={e => setRooms(e.target.value)}>
                            <option value="">Комнаты</option>
                            <option value="1">1</option>
                            <option value="2">2</option>
                            <option value="3">3</option>
                            <option value="4">4+</option>
                        </select>
                    )}
                </div>

                <div className="controls">
                    <span>Индекс:</span>
                    <button className={priceIndex === 'TOTAL' ? 'active' : ''} onClick={() => setPriceIndex('TOTAL')}>
                        Общая цена
                    </button>
                    <button className={priceIndex === 'PER_M2' ? 'active' : ''} onClick={() => setPriceIndex('PER_M2')}>
                        Цена за м^2
                    </button>
                </div>

                <div className="chart-wrapper horizontal-scroll">
                    <div className="chart-inner">
                        <ResponsiveContainer width="100%" height={420}>
                            <LineChart data={priceDynamicsData} margin={{top: 40, left: 20, right: 40}}>
                                <XAxis dataKey="date"/>
                                <YAxis yAxisId="left"/>
                                <YAxis yAxisId="right" orientation="right"/>

                                <text x={40} y={20} fill="#2196f3" fontWeight="600">
                                    {priceIndex === 'PER_M2' ? 'USD / м²' : 'USD'}
                                </text>
                                <text x="95%" y={20} fill="#ff9800" textAnchor="end" fontWeight="600">
                                    Предложения
                                </text>

                                <Tooltip/>
                                <Legend/>

                                <Line
                                    yAxisId="left"
                                    type="monotone"
                                    dataKey="price"
                                    name="Цена"
                                    stroke="#2196f3"
                                    strokeWidth={3}
                                    dot={false}
                                />

                                <Line
                                    yAxisId="right"
                                    type="monotone"
                                    dataKey="count"
                                    name="Похожие предложения"
                                    stroke="#ff9800"
                                    strokeWidth={2}
                                    strokeDasharray="6 6"
                                    dot={false}
                                />
                            </LineChart>
                        </ResponsiveContainer>
                    </div>
                </div>
            </section>

            {/* ================= ГРАФИК 2 ================= */}
            <section className="analytics-block">
                <h3>Индекс цен предложения квартир по Беларуси</h3>

                {apartmentSummary && (
                    <div className="analytics-cards">
                        <div className="analytics-card">
                            <span className="card-title">Индекс предложения</span>
                            <span className="card-value blue">
            {apartmentSummary.lastIndex.toFixed(0)} USD/м²
        </span>
                        </div>

                        <div className="analytics-card">
                            <span className="card-title">Месячное изменение</span>
                            <span
                                className={
                                    'card-value ' +
                                    (apartmentSummary.monthChangePercent >= 0 ? 'green' : 'red')
                                }
                            >
            {apartmentSummary.monthChangePercent >= 0 ? '+' : ''}
                                {apartmentSummary.monthChangePercent.toFixed(2)}%
        </span>
                        </div>

                        <div className="analytics-card">
                            <span className="card-title">Изменение за период</span>
                            <span
                                className={
                                    'card-value ' +
                                    (apartmentSummary.totalChangePercent >= 0 ? 'green' : 'red')
                                }
                            >
            {apartmentSummary.totalChangePercent >= 0 ? '+' : ''}
                                {apartmentSummary.totalChangePercent.toFixed(2)}%
        </span>
                        </div>
                    </div>

                )}

                <div className="chart-wrapper horizontal-scroll">
                    <div className="chart-inner">
                        <ResponsiveContainer width="100%" height={420}>
                            <LineChart data={apartmentIndexData} margin={{top: 40, left: 20, right: 40}}>
                                <XAxis dataKey="month"/>

                                <YAxis/>

                                <text x={40} y={20} fontWeight="600">
                                    USD / м²
                                </text>

                                <Tooltip/>
                                <Legend/>

                                <Line
                                    type="monotone"
                                    dataKey="secondaryIndex"
                                    name="Вторичный рынок"
                                    stroke="red"
                                    strokeWidth={3}
                                    dot={false}
                                />

                                <Line
                                    type="monotone"
                                    dataKey="newbuildIndex"
                                    name="Новостройки"
                                    stroke="orange"
                                    strokeWidth={3}
                                    dot={false}
                                />
                            </LineChart>
                        </ResponsiveContainer>
                    </div>
                </div>
            </section>

            {/* ================= ГРАФИК 3 ================= */}
            <section className="analytics-block">
                <h3>Средние цены на аренду недвижимости в Беларуси</h3>

                {rentSummary && (
                    <div className="analytics-cards">
                        <div className="analytics-card">
                            <span className="card-title">1-комнатные</span>
                            <span
                                className={
                                    'card-value ' +
                                    (rentSummary.oneRoom.monthChange >= 0 ? 'green' : 'red')
                                }
                            >
                    {rentSummary.oneRoom.monthChange >= 0 ? '+' : ''}
                                {rentSummary.oneRoom.monthChange.toFixed(2)}%
                                {' / '}
                                {rentSummary.oneRoom.totalChange >= 0 ? '+' : ''}
                                {rentSummary.oneRoom.totalChange.toFixed(2)}%
                </span>
                            <small>проценты: месяц / год</small>
                        </div>

                        <div className="analytics-card">
                            <span className="card-title">2-комнатные</span>
                            <span
                                className={
                                    'card-value ' +
                                    (rentSummary.twoRooms.monthChange >= 0 ? 'green' : 'red')
                                }
                            >
                    {rentSummary.twoRooms.monthChange >= 0 ? '+' : ''}
                                {rentSummary.twoRooms.monthChange.toFixed(2)}%
                                {' / '}
                                {rentSummary.twoRooms.totalChange >= 0 ? '+' : ''}
                                {rentSummary.twoRooms.totalChange.toFixed(2)}%
                </span>
                            <small>проценты: месяц / год</small>
                        </div>

                        <div className="analytics-card">
                            <span className="card-title">3-комнатные</span>
                            <span
                                className={
                                    'card-value ' +
                                    (rentSummary.threeRooms.monthChange >= 0 ? 'green' : 'red')
                                }
                            >
                    {rentSummary.threeRooms.monthChange >= 0 ? '+' : ''}
                                {rentSummary.threeRooms.monthChange.toFixed(2)}%
                                {' / '}
                                {rentSummary.threeRooms.totalChange >= 0 ? '+' : ''}
                                {rentSummary.threeRooms.totalChange.toFixed(2)}%
                </span>
                            <small>проценты: месяц / год</small>
                        </div>
                    </div>
                )}

                <div className="chart-wrapper horizontal-scroll">
                    <div className="chart-inner">
                        <ResponsiveContainer width="100%" height={420}>
                            <LineChart data={rentIndexData} margin={{top: 40, left: 20, right: 40}}>
                                <XAxis dataKey="month"/>
                                <YAxis/>

                                <text x={40} y={20} fontWeight="600">
                                    USD
                                </text>

                                <Tooltip/>
                                <Legend/>

                                <Line
                                    type="monotone"
                                    dataKey="oneRoom"
                                    name="1-комнатные"
                                    stroke="#2196f3"
                                    strokeWidth={3}
                                    dot={false}
                                />

                                <Line
                                    type="monotone"
                                    dataKey="twoRooms"
                                    name="2-комнатные"
                                    stroke="#4caf50"
                                    strokeWidth={3}
                                    dot={false}
                                />

                                <Line
                                    type="monotone"
                                    dataKey="threeRooms"
                                    name="3-комнатные"
                                    stroke="#ff9800"
                                    strokeWidth={3}
                                    dot={false}
                                />
                            </LineChart>
                        </ResponsiveContainer>
                    </div>
                </div>
            </section>


            <section className="analytics-block">
                <h3>Параметры по агентам недвижимости</h3>

                <div className="charts-row">
                    <div className="chart-card">
                        <h4>Активные предложения</h4>
                        {agentOffersData.length > 0 && (
                            <ResponsiveContainer width="100%" height={300}>
                                <PieChart>
                                    <Pie
                                        data={agentOffersData}
                                        dataKey="percent"
                                        nameKey="agentName"  // <- ИЗМЕНИТЬ ТУТ
                                        cx="50%"
                                        cy="50%"
                                        outerRadius={100}
                                        label={({
                                                    agentName,
                                                    percent
                                                }) => `${agentName}: ${percent.toFixed(1)}%`}  // <- И ТУТ
                                    >
                                        {agentOffersData.map((entry, index) => (
                                            <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]}/>
                                        ))}
                                    </Pie>
                                    <Tooltip formatter={(value) => `${value.toFixed(1)}%`}/>
                                    <Legend verticalAlign="bottom" height={36}/>
                                </PieChart>
                            </ResponsiveContainer>
                        )}

                    </div>

                    <div className="chart-card">
                        <h4>Реализованные сделки</h4>
                        <ResponsiveContainer width="100%" height={300}>
                            <PieChart>
                                <Pie
                                    data={agentArchivedData}
                                    dataKey="percent"
                                    nameKey="agentName"  // <- ИЗМЕНИТЬ ТУТ
                                    cx="50%"
                                    cy="50%"
                                    outerRadius={100}
                                    label={({
                                                agentName,
                                                percent
                                            }) => `${agentName}: ${percent.toFixed(1)}%`}  // <- И ТУТ
                                >
                                    {agentArchivedData.map((entry, index) => (
                                        <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]}/>
                                    ))}
                                </Pie>
                                <Tooltip formatter={(value) => `${value.toFixed(1)}%`}/>
                                <Legend verticalAlign="bottom" height={36}/>
                            </PieChart>
                        </ResponsiveContainer>
                    </div>
                </div>
            </section>
            <section className="analytics-block estimate-cta">
                <h3>Хотите узнать стоимость своей недвижимости?</h3>
                <p>Бесплатная онлайн-оценка на основе реальных предложений и сделок</p>

                <Link to="/estimate" className="estimate-button">
                    Рассчитать стоимость недвижимости
                </Link>
            </section>

        </div>
    );
}

export default Analytics;
