import { SET_SUBMISSION, CLEAR_SUBMISSION } from "./types";

const initialState = {
	id: "",
	author: "",
	score: 0,
	originalText: "",
	editedText: ""
};

export function submissionDetailsReducer(state = initialState, action) {
	switch (action.type) {
		case SET_SUBMISSION: {
			return { ...state, ...action.submission };
		}
		case CLEAR_SUBMISSION: {
			return { ...state, ...initialState };
		}
		default: {
			return state;
		}
	}
}
