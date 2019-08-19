import axios from "axios";
import { SET_SUBMISSION, CLEAR_SUBMISSION } from "./types";

const apiUrl = "http://192.168.1.11:8080";

export const fetchSubmission = id => {
	return async dispatch => {
		const { data } = await axios.get(`${apiUrl}/ui/submissions/${id}`);
		dispatch(setSubmission(data));
	};
};

export const setSubmission = submission => ({
	type: SET_SUBMISSION,
	submission
});

export const clearSubmission = () => ({
	type: CLEAR_SUBMISSION
});
