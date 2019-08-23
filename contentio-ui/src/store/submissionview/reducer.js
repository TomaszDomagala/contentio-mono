import {
	SET_SUBMISSION_VIEW,
	SET_SUBMISSION_SENTENCES,
	SET_CURRENT_SENTENCE
} from "./types";

const initialState = {
	details: {},
	sentences: [],
	currentSentenceIndex: null
};

export function submissionViewReducer(state = initialState, action) {
	switch (action.type) {
		case SET_SUBMISSION_VIEW: {
			return { ...state, submissionDetails: action.payload };
		}
		case SET_SUBMISSION_SENTENCES: {
			return { ...state, sentences: action.payload };
		}
		case SET_CURRENT_SENTENCE: {
			return { ...state, currentSentenceIndex: action.payload };
		}
		default: {
			return state;
		}
	}
}
