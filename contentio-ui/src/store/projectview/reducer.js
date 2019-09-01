import {
	SET_PROJECT_DETAILS,
	CLEAR_PROJECT_DETAILS,
	SET_PROJECT_MEDIA_STATUS
} from "./types";

const initialState = {
	details: {
		id: 0,
		title: "",
		predictedDuration: 0,
		audioDuration: 0
	},
	mediaStatus: {},
	submissions: []
};

export function projectViewReducer(state = initialState, action) {
	switch (action.type) {
		case SET_PROJECT_DETAILS: {
			const {
				id,
				title,
				predictedDuration,
				audioDuration,
				submissions
			} = action.payload;
			return {
				...state,
				details: {
					id,
					title,
					predictedDuration,
					audioDuration
				},
				submissions
			};
		}
		case SET_PROJECT_MEDIA_STATUS: {
			return { ...state, mediaStatus: action.payload };
		}
		case CLEAR_PROJECT_DETAILS: {
			return { ...initialState };
		}

		default: {
			return state;
		}
	}
}
