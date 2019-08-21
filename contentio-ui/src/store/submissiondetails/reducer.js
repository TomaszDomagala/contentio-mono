import { SET_SUBMISSION, CLEAR_SUBMISSION } from "./types";

const initialState = {
	selected: false,
	submission: {
		id: "",
		author: "",
		score: 0,
		originalText: "",
		editedText: ""
	}
};

export function submissionDetailsReducer(state = initialState, action) {
	switch (action.type) {
		case SET_SUBMISSION: {
			return { ...state, submission: action.submission, selected: true };
		}
		case CLEAR_SUBMISSION: {
			return { ...state, ...initialState };
		}
		default: {
			return state;
		}
	}
}
