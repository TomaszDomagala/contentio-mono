import axios from "axios";
import {
	SET_PROJECT_DETAILS,
	CLEAR_PROJECT_DETAILS,
	SET_SUBMISSION_VIEW
} from "./types";
import { apiUrl } from "../../utils/urls";

export const fetchProjectDetails = projectId => {
	return async dispatch => {
		const { data: project } = await axios.get(
			`${apiUrl}/ui/projects/${projectId}`
		);
		dispatch(setProjectDetails(project));

		const { id: submissionId } = project.submissions.find(
			subm => subm.orderInProject === 0
		);
		const { data: submission } = await axios.get(
			`${apiUrl}/ui/submissions/${submissionId}`
		);
		dispatch(setSubmissionView(submission));
	};
};

export const setProjectDetails = details => ({
	type: SET_PROJECT_DETAILS,
	payload: details
});

export const clearProjectDetails = () => ({
	type: CLEAR_PROJECT_DETAILS
});

export const setSubmissionView = submission => ({
	type: SET_SUBMISSION_VIEW,
	payload: submission
});
