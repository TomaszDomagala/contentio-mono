import {
  SET_DETAILS_TITLE,
  SET_DETAILS_SUBMISSIONS,
  SET_SUBMISSION_STATEMENT
} from "./types";

const initialState = {
  title: "",
  submissions: []
};

export function projectDetailsReducer(state = initialState, action) {
  switch (action.type) {
    case SET_DETAILS_TITLE: {
      return { ...state, title: action.title };
    }
    case SET_DETAILS_SUBMISSIONS: {
      return { ...state, submissions: action.submissions };
    }
    case SET_SUBMISSION_STATEMENT: {
      const { submissionId: id, statement } = action;
      const submissions = state.submissions.map(submission => {
        if (id === submission.id) {
          return { ...submission, statement };
        } else {
          return submission;
        }
      });
      return { ...state, submissions };
    }
    default: {
      return state;
    }
  }
}
