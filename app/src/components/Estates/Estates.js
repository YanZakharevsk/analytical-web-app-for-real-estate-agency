import './Estates.css';
import { useState, useEffect, useCallback, useMemo } from 'react';
import PhotosFetcher from '../../PhotosFetcher.js';
import heart from './heart.png';
import redHeart from './heart-red.png';
import details from './file.png';
import filter from './filter.png';
import { Link, useSearchParams } from 'react-router-dom';
import { useAuth } from '../AuthContext.js';
import { trackEvent, getCurrentScreenOrigin } from '../../utils/analyticsTrack';
import { readApiErrorMessage } from '../../utils/readApiError.js';

const DEFAULT_PAGE_SIZE = 12;

const emptyCriteria = () => ({
    type: '',
    bathrooms: null,
    rooms: null,
    garage: null,
    storey: null,
    location: '',
    balcony: null,
    availability: '',
    condition: '',
    priceFrom: null,
    priceTo: null,
});

function parseCriteriaFromSearchParams(searchParams) {
    const num = (key) => {
        const v = searchParams.get(key);
        if (v === null || v === '') {
            return null;
        }
        const n = Number(v);
        return Number.isFinite(n) ? n : null;
    };
    const bool = (key) => {
        if (!searchParams.has(key)) {
            return null;
        }
        const v = searchParams.get(key);
        if (v === 'true') {
            return true;
        }
        if (v === 'false') {
            return false;
        }
        return null;
    };
    const c = emptyCriteria();
    c.type = searchParams.get('type') || '';
    c.bathrooms = num('bathrooms');
    c.rooms = num('rooms');
    c.garage = bool('garage');
    c.storey = num('storey');
    c.location = searchParams.get('location') || '';
    c.balcony = bool('balcony');
    c.availability = searchParams.get('availability') || '';
    c.condition = searchParams.get('condition') || '';
    c.priceFrom = num('priceFrom');
    c.priceTo = num('priceTo');
    return c;
}

function criteriaToSearchParams(criteria, page, pageSize) {
    const p = new URLSearchParams();
    Object.entries(criteria).forEach(([key, value]) => {
        if (value === null || value === '' || value === undefined) {
            return;
        }
        p.set(key, String(value));
    });
    p.set('page', String(page));
    p.set('pageSize', String(pageSize));
    return p;
}

function buildOffersQuery(criteria, page, pageSize) {
    const params = new URLSearchParams();
    Object.entries(criteria).forEach(([key, value]) => {
        if (value === null || value === '' || value === undefined) {
            return;
        }
        params.set(key, String(value));
    });
    params.set('page', String(page));
    params.set('pageSize', String(pageSize));
    return params.toString();
}

function Estates() {
    const [searchParams, setSearchParams] = useSearchParams();
    const [offers, setOffers] = useState([]);
    const [pageMeta, setPageMeta] = useState({
        totalPages: 0,
        totalElements: 0,
        number: 0,
        size: DEFAULT_PAGE_SIZE,
    });
    const [listError, setListError] = useState(null);
    const { authenticatedUser } = useAuth();
    const [expandedDescriptions, setExpandedDescriptions] = useState({});
    const [criteria, setCriteria] = useState(() => parseCriteriaFromSearchParams(searchParams));
    const [page, setPage] = useState(() => {
        const n = parseInt(searchParams.get('page') || '0', 10);
        return Number.isFinite(n) && n >= 0 ? n : 0;
    });
    const [pageSize, setPageSize] = useState(() => {
        const n = parseInt(searchParams.get('pageSize') || String(DEFAULT_PAGE_SIZE), 10);
        const clamped = Number.isFinite(n) ? Math.min(50, Math.max(1, n)) : DEFAULT_PAGE_SIZE;
        return clamped;
    });
    const [filters, setFilters] = useState(false);

    const types = useMemo(
        () => [
            ['APARTMENT', 'Квартира'],
            ['BUNGALOW', 'Частный дом'],
            ['COTTAGE', 'Коттедж'],
            ['MANSION', 'Особняк'],
        ],
        []
    );

    const availabilities = useMemo(
        () => [
            ['FOR_SALE', 'Продажа'],
            ['FOR_RENT', 'Аренда'],
        ],
        []
    );

    const conditions = useMemo(
        () => [
            ['NEEDS_RENOVATION', 'Требует ремонта'],
            ['DEVELOPER_CONDITION', 'От застройщика'],
            ['AFTER_RENOVATION', 'После ремонта'],
            ['NORMAL_USE_SIGNS', 'В хорошем состоянии'],
        ],
        []
    );

    const translateEnum = (value, array) => {
        if (!value) {
            return '';
        }
        const found = array.find((item) => item[0] === value);
        return found ? found[1] : value;
    };

    useEffect(() => {
        const next = criteriaToSearchParams(criteria, page, pageSize);
        if (next.toString() === searchParams.toString()) {
            return;
        }
        setSearchParams(next, { replace: true });
    }, [criteria, page, pageSize, searchParams, setSearchParams]);

    useEffect(() => {
        const nextCriteria = parseCriteriaFromSearchParams(searchParams);
        const nextPage = Math.max(0, parseInt(searchParams.get('page') || '0', 10) || 0);
        const rawSize = parseInt(searchParams.get('pageSize') || String(DEFAULT_PAGE_SIZE), 10);
        const nextSize = Number.isFinite(rawSize) ? Math.min(50, Math.max(1, rawSize)) : DEFAULT_PAGE_SIZE;

        setCriteria((prev) => (JSON.stringify(prev) === JSON.stringify(nextCriteria) ? prev : nextCriteria));
        setPage((p) => (p === nextPage ? p : nextPage));
        setPageSize((s) => (s === nextSize ? s : nextSize));
    }, [searchParams]);

    useEffect(() => {
        const fetchOffers = async () => {
            const qs = buildOffersQuery(criteria, page, pageSize);
            setListError(null);
            try {
                const response = await fetch('/api/auth/offers?' + qs, { method: 'GET' });

                if (!response.ok) {
                    setListError(await readApiErrorMessage(response));
                    setOffers([]);
                    setPageMeta({ totalPages: 0, totalElements: 0, number: page, size: pageSize });
                    return;
                }

                const data = await response.json();
                const content = Array.isArray(data.content) ? data.content : [];
                const resultsCount = content.length;
                const totalElements = typeof data.totalElements === 'number' ? data.totalElements : content.length;
                const totalPages = typeof data.totalPages === 'number' ? data.totalPages : 0;
                const number = typeof data.number === 'number' ? data.number : page;
                const size = typeof data.size === 'number' ? data.size : pageSize;

                trackEvent(
                    {
                        eventName: 'search_performed',
                        category: 'SYSTEM',
                        properties: {
                            search_query: JSON.stringify(criteria),
                            results_count: resultsCount,
                            page: number,
                        },
                    },
                    authenticatedUser?.token
                );
                if (totalElements === 0) {
                    trackEvent(
                        {
                            eventName: 'search_no_results',
                            category: 'SYSTEM',
                            properties: {
                                search_query: JSON.stringify(criteria),
                                filters_applied: Object.values(criteria).filter((v) => v !== '' && v !== null).length,
                            },
                        },
                        authenticatedUser?.token
                    );
                }

                const fetchOffersWithPhotos = async (offer) => {
                    const photos = await PhotosFetcher(offer.estateID);
                    return {
                        info: offer,
                        photos: photos && photos.length ? photos : [null],
                    };
                };

                setOffers(await Promise.all(content.filter((o) => o != null).map(fetchOffersWithPhotos)));
                setPageMeta({ totalPages, totalElements, number, size });
            } catch (error) {
                console.error('Error fetching offers:', error);
                setListError('Не удалось загрузить объявления.');
                setOffers([]);
            }
        };
        fetchOffers();
    }, [criteria, page, pageSize, authenticatedUser?.token]);

    function numberWithCommas(x) {
        if (x == null || x === '') {
            return '';
        }
        return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ' ');
    }

    const applyCriteriaPatch = useCallback((patch) => {
        setCriteria((prev) => ({ ...prev, ...patch }));
        setPage(0);
    }, []);

    const handleFilterChange = (e) => {
        const { name, value, type, checked } = e.target;
        trackEvent(
            {
                eventName: 'search_filter_apply',
                category: 'INTERACTION',
                properties: {
                    filter_type: name || 'unknown',
                    filter_value: type === 'checkbox' ? checked : value,
                },
            },
            authenticatedUser?.token
        );

        if (type === 'checkbox') {
            applyCriteriaPatch({ [name]: checked ? true : null });
            return;
        }
        if (type === 'number') {
            const v = value === '' ? null : Number(value);
            applyCriteriaPatch({ [name]: Number.isFinite(v) ? v : null });
            return;
        }
        applyCriteriaPatch({ [name]: value });
    };

    const addToFavorites = (id) => {
        if (!authenticatedUser?.token) {
            alert('Сначала войдите в систему.');
            trackEvent({
                eventName: 'access_denied',
                category: 'SYSTEM',
                properties: {
                    required_role: 'CUSTOMER',
                    current_role: 'ANONYMOUS',
                },
            });
            return;
        }

        const add = async () => {
            const response = await fetch(`/api/customer/add-to-favorites?id=${id}`, {
                method: 'POST',
                headers: {
                    Authorization: 'Bearer ' + authenticatedUser.token,
                },
            });

            if (!response.ok) {
                console.log('Failed to add the offer to favorites');
                return;
            }
            trackEvent(
                {
                    eventName: 'add_to_favorites_click',
                    category: 'INTERACTION',
                    properties: {
                        property_id: id,
                        screen_origin: getCurrentScreenOrigin(),
                    },
                },
                authenticatedUser?.token
            );
            trackEvent(
                {
                    eventName: 'property_added_to_favorites',
                    category: 'CONVERSION',
                    properties: {
                        property_id: id,
                        screen_origin: getCurrentScreenOrigin(),
                    },
                },
                authenticatedUser?.token
            );

            const image = document.querySelector(`.heart${id}`);
            if (image) {
                image.setAttribute('src', redHeart);
            }
        };

        add();
    };

    const toggleDescription = (id) => {
        setExpandedDescriptions((prev) => ({
            ...prev,
            [id]: !prev[id],
        }));
    };

    const onCheckDetailsClick = () => {
        if (!authenticatedUser?.token) {
            alert('Сначала войдите в систему.');
            trackEvent({
                eventName: 'access_denied',
                category: 'SYSTEM',
                properties: {
                    required_role: 'CUSTOMER',
                    current_role: 'ANONYMOUS',
                },
            });
        }
    };

    const canPrev = pageMeta.number > 0;
    const canNext = pageMeta.number < pageMeta.totalPages - 1;

    return (
        <div className="re-page estates-page">
            <section className="estates-hero">
                <div className="estates-hero-inner">
                    <h1>Каталог недвижимости</h1>
                    <p className="estates-hero-sub">
                        Эксклюзивные предложения с удобными фильтрами и постраничным просмотром.
                    </p>
                </div>
            </section>

            <div className="estates-inner">
                <p className="estates-intro">
                    Добро пожаловать в раздел «Недвижимость»: подберите объект по параметрам — выбранные фильтры
                    сохраняются в адресе страницы и восстанавливаются после перезагрузки.
                </p>

                <div className="estates-filters-bar">
                    <button
                        type="button"
                        className="estates-filter-toggle"
                        onClick={() => setFilters(!filters)}
                        aria-expanded={filters}
                    >
                        <img src={filter} alt="" width={24} height={24} />
                        Фильтры
                    </button>
                </div>

                {filters && (
                    <div className="estates-filters-panel">
                        <div className="estates-select-row">
                            <label className="estates-field">
                                <span>Доступность</span>
                                <select name="availability" value={criteria.availability} onChange={handleFilterChange}>
                                    <option value="">Любая</option>
                                    {availabilities.map(([val, label]) => (
                                        <option key={val} value={val}>
                                            {label}
                                        </option>
                                    ))}
                                </select>
                            </label>
                            <label className="estates-field">
                                <span>Состояние</span>
                                <select name="condition" value={criteria.condition} onChange={handleFilterChange}>
                                    <option value="">Любое</option>
                                    {conditions.map(([val, label]) => (
                                        <option key={val} value={val}>
                                            {label}
                                        </option>
                                    ))}
                                </select>
                            </label>
                            <label className="estates-field">
                                <span>Тип</span>
                                <select name="type" value={criteria.type} onChange={handleFilterChange}>
                                    <option value="">Любой</option>
                                    {types.map(([val, label]) => (
                                        <option key={val} value={val}>
                                            {label}
                                        </option>
                                    ))}
                                </select>
                            </label>
                        </div>
                        <div className="estates-grid-inputs">
                            <label className="estates-field" htmlFor="estates-bathrooms">
                                Ванные
                                <input
                                    id="estates-bathrooms"
                                    name="bathrooms"
                                    type="number"
                                    min={0}
                                    value={criteria.bathrooms ?? ''}
                                    onChange={handleFilterChange}
                                />
                            </label>
                            <label className="estates-field" htmlFor="estates-storey">
                                Этаж
                                <input
                                    id="estates-storey"
                                    name="storey"
                                    type="number"
                                    min={0}
                                    value={criteria.storey ?? ''}
                                    onChange={handleFilterChange}
                                />
                            </label>
                            <label className="estates-field" htmlFor="estates-rooms">
                                Комнаты
                                <input
                                    id="estates-rooms"
                                    name="rooms"
                                    type="number"
                                    min={0}
                                    value={criteria.rooms ?? ''}
                                    onChange={handleFilterChange}
                                />
                            </label>
                            <label className="estates-field" htmlFor="estates-priceFrom">
                                Цена от
                                <input
                                    id="estates-priceFrom"
                                    name="priceFrom"
                                    type="number"
                                    min={0}
                                    step={1000}
                                    value={criteria.priceFrom ?? ''}
                                    onChange={handleFilterChange}
                                />
                            </label>
                            <label className="estates-field" htmlFor="estates-priceTo">
                                Цена до
                                <input
                                    id="estates-priceTo"
                                    name="priceTo"
                                    type="number"
                                    min={0}
                                    step={1000}
                                    value={criteria.priceTo ?? ''}
                                    onChange={handleFilterChange}
                                />
                            </label>
                        </div>
                        <div className="estates-row-mixed">
                            <label className="estates-check">
                                <input
                                    name="balcony"
                                    type="checkbox"
                                    checked={criteria.balcony === true}
                                    onChange={handleFilterChange}
                                />
                                Балкон
                            </label>
                            <label className="estates-check">
                                <input
                                    name="garage"
                                    type="checkbox"
                                    checked={criteria.garage === true}
                                    onChange={handleFilterChange}
                                />
                                Гараж
                            </label>
                            <label className="estates-field estates-field-grow" htmlFor="estates-location">
                                Местоположение
                                <input
                                    id="estates-location"
                                    name="location"
                                    type="text"
                                    value={criteria.location}
                                    onChange={handleFilterChange}
                                    placeholder="Город или район"
                                />
                            </label>
                        </div>
                    </div>
                )}

                {listError && <div className="estates-alert estates-alert-error">{listError}</div>}

                <ul className="estates-list">
                    {offers.length > 0 ? (
                        offers.map((offer) => (
                            <li key={offer.info.id}>
                                <article className="estates-card">
                                    <div className="estates-card-media">
                                        {offer.photos[0] ? (
                                            <img className="estates-card-photo" src={offer.photos[0]} alt="" />
                                        ) : (
                                            <div className="estates-card-photo estates-card-photo-placeholder">Нет фото</div>
                                        )}
                                    </div>
                                    <div className="estates-card-body">
                                        <h3>
                                            {offer.info.location} · {translateEnum(offer.info.availability, availabilities)}
                                        </h3>
                                        <p className="estates-card-price">${numberWithCommas(offer.info.price)}</p>
                                        <div className="estates-description">
                                            <p className={expandedDescriptions[offer.info.id] ? 'expanded' : ''}>
                                                {offer.info.description || '—'}
                                            </p>
                                            {(offer.info.description || '').length > 120 && (
                                                <button
                                                    type="button"
                                                    className="estates-toggle-desc"
                                                    onClick={() => toggleDescription(offer.info.id)}
                                                >
                                                    {expandedDescriptions[offer.info.id] ? 'Свернуть' : 'Показать больше'}
                                                </button>
                                            )}
                                        </div>
                                        <div className="estates-card-actions">
                                            <button type="button" className="estates-btn">
                                                <Link
                                                    to={`/check-details/${offer.info.id}`}
                                                    onClick={() => {
                                                        onCheckDetailsClick();
                                                        trackEvent(
                                                            {
                                                                eventName: 'property_card_click',
                                                                category: 'INTERACTION',
                                                                properties: {
                                                                    property_id: offer.info.id,
                                                                    screen_origin: getCurrentScreenOrigin(),
                                                                },
                                                            },
                                                            authenticatedUser?.token
                                                        );
                                                    }}
                                                >
                                                    <img src={details} width={22} height={22} alt="" />
                                                </Link>
                                                Подробнее
                                            </button>
                                            <button
                                                type="button"
                                                className="estates-btn"
                                                onClick={() => addToFavorites(offer.info.id)}
                                            >
                                                <img className={'heart' + offer.info.id} src={heart} width={22} height={22} alt="" />
                                                В избранное
                                            </button>
                                        </div>
                                    </div>
                                </article>
                            </li>
                        ))
                    ) : (
                        !listError && <p className="estates-empty">Предложений не найдено. Попробуйте изменить критерии.</p>
                    )}
                </ul>

                {pageMeta.totalPages > 1 && (
                    <nav className="estates-pagination" aria-label="Страницы результатов">
                        <button type="button" className="estates-page-btn" disabled={!canPrev} onClick={() => setPage((p) => Math.max(0, p - 1))}>
                            Назад
                        </button>
                        <span className="estates-page-info">
                            Страница {pageMeta.number + 1} из {pageMeta.totalPages} ({pageMeta.totalElements} объявлений)
                        </span>
                        <button
                            type="button"
                            className="estates-page-btn"
                            disabled={!canNext}
                            onClick={() => setPage((p) => p + 1)}
                        >
                            Вперёд
                        </button>
                        <label className="estates-page-size">
                            На странице
                            <select
                                value={pageSize}
                                onChange={(e) => {
                                    setPageSize(Number(e.target.value));
                                    setPage(0);
                                }}
                            >
                                {[6, 12, 24, 50].map((n) => (
                                    <option key={n} value={n}>
                                        {n}
                                    </option>
                                ))}
                            </select>
                        </label>
                    </nav>
                )}

                {pageMeta.totalPages <= 1 && pageMeta.totalElements > 0 && (
                    <div className="estates-pagination estates-pagination-compact">
                        <label className="estates-page-size">
                            На странице
                            <select
                                value={pageSize}
                                onChange={(e) => {
                                    setPageSize(Number(e.target.value));
                                    setPage(0);
                                }}
                            >
                                {[6, 12, 24, 50].map((n) => (
                                    <option key={n} value={n}>
                                        {n}
                                    </option>
                                ))}
                            </select>
                        </label>
                    </div>
                )}
            </div>
        </div>
    );
}

export default Estates;
