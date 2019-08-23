import axios from "axios";
import { apiUrl } from "../../utils/urls";
import {
	SET_SUBMISSION_VIEW,
	SET_SUBMISSION_SENTENCES,
	SET_CURRENT_SENTENCE
} from "./types";

export const fetchSubmissionDetails = submissionId => {
	return async dispatch => {
		const submissionReq = axios.get(
			`${apiUrl}/ui/submissions/${submissionId}`
		);
		const sentencesReq = axios.get(
			`${apiUrl}/ui/submissions/${submissionId}/sentences`
		);
		const [{ data: submission }, { data: sentences }] = await Promise.all([
			submissionReq,
			sentencesReq
		]);
		const lastSentence = sentences.reduce((prev, curr) => {
			return prev.index > curr.index ? prev : curr;
		});
		dispatch(setSubmissionView(submission));
		dispatch(setSubmissionSentences(sentences));
		dispatch(setCurrentSentence(lastSentence.index));
	};
};

export const setSubmissionView = submission => ({
	type: SET_SUBMISSION_VIEW,
	payload: submission
});
export const setSubmissionSentences = sentences => ({
	type: SET_SUBMISSION_SENTENCES,
	payload: sentences
});
export const setCurrentSentence = sentenceIndex => ({
	type: SET_CURRENT_SENTENCE,
	payload: sentenceIndex
});
