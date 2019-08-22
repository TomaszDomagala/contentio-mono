import axios from "axios";
import { SET_PROJECT_DETAILS, CLEAR_PROJECT_DETAILS } from "./types";
import { apiUrl } from "../../utils/urls";



export const fetchProjectDetails = projectId => {
	return async dispatch => {
		const response = await axios.get(`${apiUrl}/ui/projects/${projectId}`);
		dispatch(setProjectDetails(response.data));
	};
};

export const setProjectDetails = details => ({
	type: SET_PROJECT_DETAILS,
	payload:details
});

export const clearProjectDetails = () => ({
	type: CLEAR_PROJECT_DETAILS
});
