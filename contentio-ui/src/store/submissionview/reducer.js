import {
	SET_SUBMISSION_VIEW,
	SET_SUBMISSION_SENTENCES,
	SET_CURRENT_SENTENCE
} from "./types";

const initialState = {
	details: {},
	sentences: [],
	sentenceView: {
		currentIndex: null,
		first: true,
		last: true
	}
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
			const sentenceView = {
				currentIndex: action.payload,
				first: action.payload === 0,
				last: action.payload === state.sentences.length - 1
			};
			return { ...state, sentenceView };
		}
		default: {
			return state;
		}
	}
}
