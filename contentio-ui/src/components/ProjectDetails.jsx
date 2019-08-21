import React, { Component } from "react";
import { connect } from "react-redux";
import { Box, Flex, Card, Text, Heading } from "rebass";
import { fetchDetails, clearDetails } from "../store/projectdetails/actions";
import {
	fetchSubmission,
	clearSubmission
} from "../store/submissiondetails/actions";
import { formatSec } from "../utils/formatting";

class ProjectDetails extends Component {
	componentWillUnmount() {
		this.props.clearDetails();
	}

	componentDidMount() {
		const { projectId } = this.props;
		this.props.fetchDetails(projectId);
	}

	render() {
		const {
			title,
			predictedDuration,
			audioDuration,
			submissions,
			fetchSubmission
		} = this.props;
		console.log(predictedDuration);
		return (
			<Box
				p={3}
				className="no-scroll-bar"
				style={{
					maxHeight: "100vh",
					overflow: "hidden",
					overflowY: "scroll"
				}}
			>
				<Heading p={1} color="text1">
					{title}
				</Heading>
				<Box p={1}>
					<Text>
						Predicted duration {formatSec(predictedDuration)}
					</Text>
					<Text>Audio duration {formatSec(audioDuration)}</Text>
				</Box>
				<SubmissionList
					submissions={submissions}
					onItemClick={fetchSubmission}
				/>
			</Box>
		);
	}
}

const mapStateToProps = ({ projectDetailsReducer: details }) => ({
	...details
});
const mapDispatchToProps = dispatch => ({
	fetchDetails: projectId => dispatch(fetchDetails(projectId)),
	clearDetails: () => dispatch(clearDetails()),
	fetchSubmission: submissionId => dispatch(fetchSubmission(submissionId))
});
export default connect(
	mapStateToProps,
	mapDispatchToProps
)(ProjectDetails);

const SubmissionList = ({ submissions, onItemClick }) => (
	<Box>
		{submissions.map(submission => (
			<SubmissionItem
				key={submission.id}
				submission={submission}
				onClick={() => onItemClick(submission.id)}
				style={{ cursor: "pointer" }}
			/>
		))}
	</Box>
);

const SubmissionItem = props => {
	const {
		id,
		author,
		score,
		text,
		predictedDuration,
		audioDuration
	} = props.submission;
	return (
		<Card
			my={3}
			p={3}
			borderStyle="solid"
			border={1}
			borderRadius={8}
			borderColor="line"
			{...props}
		>
			<Flex>
				<Text fontSize={1} color="text2">
					u/{author}
				</Text>
				<Text mx={2} fontSize={1} color="text2">
					{id}
				</Text>
				<Text color="text2" fontSize={1}>
					{score}
				</Text>
				<Text mx={2} color="text2" fontSize={1}>
					{formatSec(predictedDuration)}/{formatSec(audioDuration)}
				</Text>
			</Flex>

			<Box mt={2}>
				<Text color="text1">{text}</Text>
			</Box>
		</Card>
	);
};
