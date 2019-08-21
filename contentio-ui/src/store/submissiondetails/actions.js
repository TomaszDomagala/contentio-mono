import axios from "axios";
import { SET_SUBMISSION, CLEAR_SUBMISSION, SET_SENTENCES } from "./types";
import { apiUrl } from "../../utils/urls";


export const fetchSubmission = id => {
	return async dispatch => {
		const submissionReq = axios.get(`${apiUrl}/ui/submissions/${id}`);
		const sentencesReq = axios.get(
			`${apiUrl}/ui/submissions/${id}/sentences`
		);
		const [submission, sentences] = await Promise.all([
			submissionReq,
			sentencesReq
		]);
		dispatch(setSubmission(submission.data));
		dispatch(setSentences(sentences.data));
	};
};

export const setSubmission = submission => ({
	type: SET_SUBMISSION,
	submission
});

export const clearSubmission = () => ({
	type: CLEAR_SUBMISSION
});

export const setSentences = sentences => ({
	type: SET_SENTENCES,
	sentences
});
