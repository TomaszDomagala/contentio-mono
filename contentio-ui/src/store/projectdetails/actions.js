import axios from 'axios'
import { SET_DETAILS_TITLE, SET_DETAILS_SUBMISSIONS, SET_SUBMISSION_STATEMENT } from './types';

const apiUrl = "http://192.168.1.11:8080"

export const fetchDetails = projectId => {
    return async dispatch => {
        const titleReq = axios.get(`${apiUrl}/projects/${projectId}/title`)
        const submissionsReq = axios.get(`${apiUrl}/projects/${projectId}/submissions`)
        const [titleRes, submissionsRes] = await Promise.all([titleReq, submissionsReq])

        dispatch(setDetailsTitle(titleRes.data))
        dispatch(setDetailsSubmissions(submissionsRes.data))
    }
}

export const setDetailsTitle = title => ({
    type: SET_DETAILS_TITLE,
    title
})

export const setDetailsSubmissions = submissions => ({
    type: SET_DETAILS_SUBMISSIONS,
    submissions
})

export const fetchStatement = submissionId => {
    return async dispatch => {
        const statementRes = await axios.get(`${apiUrl}/submissions/${submissionId}/statement`)
        dispatch(setSubmissionStatement(submissionId, statementRes.data))
    }
}

export const setSubmissionStatement = (submissionId, statement) => ({
    type: SET_SUBMISSION_STATEMENT,
    statement,
    submissionId
})