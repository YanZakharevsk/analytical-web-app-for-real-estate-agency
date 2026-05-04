import { useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { useAuth } from './AuthContext';
import { trackBatch } from '../utils/analyticsTrack';

function mapPathToScreenEvent(pathname) {
    const base = { category: 'SCREEN_VIEW' };

    if (pathname === '/homepage' || pathname === '/') {
        return { ...base, eventName: 'home_screen_view', properties: { screen_name: 'home' } };
    }
    if (pathname === '/login') {
        return { ...base, eventName: 'login_screen_view', properties: { screen_name: 'login' } };
    }
    if (pathname === '/register') {
        return { ...base, eventName: 'registration_screen_view', properties: { screen_name: 'registration' } };
    }
    if (pathname === '/estates') {
        return { ...base, eventName: 'search_screen_view', properties: { screen_name: 'search' } };
    }
    if (pathname === '/analytics') {
        return { ...base, eventName: 'analytics_dashboard_screen_view', properties: { screen_name: 'analytics_dashboard' } };
    }
    if (pathname === '/agents') {
        return { ...base, eventName: 'agents_screen_view', properties: { screen_name: 'agents' } };
    }
    if (pathname === '/report-estate') {
        return { ...base, eventName: 'add_listing_screen_view', properties: { screen_name: 'add_listing' } };
    }
    if (pathname === '/my-estates') {
        return { ...base, eventName: 'owner_properties_screen_view', properties: { screen_name: 'owner_properties' } };
    }
    if (pathname === '/reported-offers') {
        return { ...base, eventName: 'applications_management_screen_view', properties: { screen_name: 'applications_management' } };
    }
    if (pathname === '/add-slots') {
        return { ...base, eventName: 'meeting_dates_screen_view', properties: { screen_name: 'meeting_dates' } };
    }
    if (pathname === '/manage-offers') {
        return { ...base, eventName: 'deals_management_screen_view', properties: { screen_name: 'deals_management' } };
    }
    if (pathname === '/favorites') {
        return { ...base, eventName: 'notes_screen_view', properties: { screen_name: 'notes' } };
    }
    if (pathname === '/account' || pathname === '/settings') {
        return { ...base, eventName: 'profile_screen_view', properties: { screen_name: 'profile' } };
    }
    const detailsMatch = pathname.match(/^\/check-details\/(\d+)/);
    if (detailsMatch) {
        return {
            ...base,
            eventName: 'property_details_screen_view',
            properties: { screen_name: 'property_details', property_id: Number(detailsMatch[1]) }
        };
    }
    return {
        ...base,
        eventName: 'screen_view',
        properties: { screen_name: pathname.replace(/^\//, '').replace(/\//g, '_') || 'root' }
    };
}

export default function RouteAnalyticsListener() {
    const location = useLocation();
    const { authenticatedUser } = useAuth();

    useEffect(() => {
        const ev = mapPathToScreenEvent(location.pathname);
        if (ev) {
            trackBatch([ev], authenticatedUser?.token);
        }
    }, [location.pathname, authenticatedUser?.token]);

    return null;
}
