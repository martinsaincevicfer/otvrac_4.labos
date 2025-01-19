import axios from 'axios';
import { useAuth0 } from '@auth0/auth0-react';

function RefreshFilesButton() {
    const { getAccessTokenSilently } = useAuth0();

    const refreshFiles = async () => {
        try {
            const token = await getAccessTokenSilently();
            const response = await axios.post('http://localhost:8080/api/serije/refresh-files', {}, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            alert(response.data.message);
        } catch (error) {
            console.error("There was an error refreshing the files!", error);
            alert("Failed to refresh files");
        }
    };

    return (
        <button onClick={refreshFiles}>Osvježi preslike</button>
    );
}

export default RefreshFilesButton;
