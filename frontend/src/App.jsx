import './App.css';
import {BrowserRouter, Navigate, Route, Routes} from "react-router-dom";
import Index from "./components/Index.jsx";
import Datatable from "./components/Datatable.jsx";
import { Auth0Provider } from '@auth0/auth0-react';
import Profile from "./components/Profile.jsx";
import configJson from "./auth_config.json";

const onRedirectCallback = (appState) => {
    window.history.replaceState(
        {},
        document.title,
        appState?.returnTo || window.location.pathname
    );
};

function App() {
    return (
        <Auth0Provider domain={configJson.domain}
                       clientId={configJson.clientId}
                       onRedirectCallback={onRedirectCallback}
                       authorizationParams={{ redirect_uri: window.location.origin + "/index", audience: configJson.audience }}
                       cacheLocation="localstorage"
                       useRefreshTokens={true}>
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<Navigate to="/index" />} />
                    <Route path="/datatable" Component={Datatable}/>
                    <Route path="/index" Component={Index}/>
                    <Route path="/profile" Component={Profile}/>
                </Routes>
            </BrowserRouter>
        </Auth0Provider>
    )
}

export default App