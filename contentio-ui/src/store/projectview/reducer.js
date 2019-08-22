import {
	SET_PROJECT_DETAILS,
	CLEAR_PROJECT_DETAILS,
	SET_SUBMISSION_VIEW
} from "./types";

const initialState = {
	details: {
		title: "",
		predictedDuration: 0,
		audioDuration: 0
	},
	submissions: [],
	submissionDetails: {}
};

export function projectViewReducer(state = initialState, action) {
	switch (action.type) {
		case SET_PROJECT_DETAILS: {
			const {
				title,
				predictedDuration,
				audioDuration,
				submissions
			} = action.payload;
			return {
				...state,
				details: {
					title,
					predictedDuration,
					audioDuration
				},
				submissions
			};
		}
		case SET_SUBMISSION_VIEW: {
			return { ...state, submissionDetails: action.payload };
		}

		case CLEAR_PROJECT_DETAILS: {
			return { ...initialState };
		}

		default: {
			return state;
		}
	}
}
