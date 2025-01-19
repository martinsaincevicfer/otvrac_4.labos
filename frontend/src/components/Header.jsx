import { useAuth0 } from '@auth0/auth0-react';
import {useEffect, useState} from "react";
import '../styles/header.css';

const Header = () => {
    const { loginWithRedirect, logout, isAuthenticated, error } = useAuth0();
    const [authState, setAuthState] = useState(isAuthenticated);

    useEffect(() => {
        setAuthState(isAuthenticated);
    }, [isAuthenticated]);

    useEffect(() => {
        if (error) {
            console.error('Authentication error:', error);
            alert(`Authentication error: ${error.message}`);
        }
    }, [error]);

    return (
        <header>
            <div className="left-buttons">
                <button onClick={() => window.location.href = '/index'}>Index</button>
                <button onClick={() => window.location.href = '/datatable'}>Datatable</button>
            </div>
            <div className="right-buttons">
                {!authState ? (
                    <button onClick={() => loginWithRedirect()}>Prijava</button>
                ) : (
                    <>
                        <button onClick={() => window.location.href = '/profile'}>Korisnički profil</button>
                        <button onClick={() => logout({ returnTo: window.location.origin + "/index" })}>Odjava</button>
                    </>
                )}
            </div>
        </header>
    );
};

export default Header;