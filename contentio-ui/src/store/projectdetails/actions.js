import axios from "axios";
import { SET_DETAILS, CLEAR_DETAILS } from "./types";

const apiUrl = "http://192.168.1.11:8080";

export const fetchDetails = projectId => {
	return async dispatch => {
		const response = await axios.get(`${apiUrl}/ui/projects/${projectId}`);
		dispatch(setDetails(response.data));
	};
};

export const setDetails = details => ({
	type: SET_DETAILS,
	details
});

export const clearDetails = () => ({
	type: CLEAR_DETAILS
});
