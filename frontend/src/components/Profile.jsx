import {useAuth0} from "@auth0/auth0-react";
import Header from "./Header.jsx";
import '../styles/profile.css';

const Profile = () => {
    const { user, isAuthenticated, isLoading } = useAuth0();

    if (isLoading) {
        return <div>Loading ...</div>;
    }

    if (!isAuthenticated) {
        return (
            <>
                <Header />
                <div>You don't have the permission to view this site</div>
            </>
        );
    }

    return (
        isAuthenticated && (
            <>
                <Header />
                <div>
                    <img src={user.picture} alt={user.name} />
                    <h2>{user.name}</h2>
                    <p>{user.email}</p>
                </div>
            </>
        )
    );
};

export default Profile;