import axios from "axios";
import {
	SET_PROJECT_DETAILS,
	CLEAR_PROJECT_DETAILS,
	SET_PROJECT_MEDIA_STATUS
} from "./types";
import { fetchSubmissionDetails } from "../submissionview/actions";
import { apiUrl } from "../../utils/urls";

export const fetchProjectDetails = projectId => {
	return async dispatch => {
		const detailsReq = axios.get(`${apiUrl}/ui/projects/${projectId}`);
		const mediaStatusReq = axios.get(
			`${apiUrl}/projects/${projectId}/mediastatus`
		);
		const [{ data: details }, { data: mediaStatus }] = await Promise.all([
			detailsReq,
			mediaStatusReq
		]);
		dispatch(setProjectDetails(details));
		dispatch(setProjectMediaStatus(mediaStatus));
		const { id: submissionId } = details.submissions.find(
			subm => subm.orderInProject === 0
		);
		dispatch(fetchSubmissionDetails(submissionId));
	};
};

export const setProjectDetails = details => ({
	type: SET_PROJECT_DETAILS,
	payload: details
});

export const setProjectMediaStatus = mediaStatus => ({
	type: SET_PROJECT_MEDIA_STATUS,
	payload: mediaStatus
});

export const clearProjectDetails = () => ({
	type: CLEAR_PROJECT_DETAILS
});
