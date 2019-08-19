import React, { Component } from "react";
import { connect } from "react-redux";
import { Box, Flex, Card, Text, Heading } from "rebass";
import { fetchDetails, clearDetails } from "../store/projectdetails/actions";

class SubmissionItem extends Component {
	render() {
		const { id, author, score, text, duration } = this.props.submission;
		return (
			<Card
				my={3}
				p={3}
				borderStyle="solid"
				border={1}
				borderRadius={8}
				borderColor="line"
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
						{duration.toFixed(2)} sec
					</Text>
				</Flex>

				<Box mt={2}>
					<Text color="text1">{text}</Text>
				</Box>
			</Card>
		);
	}
}

class ProjectDetails extends Component {
	componentWillMount() {
		this.props.clearDetails();
	}

	componentDidMount() {
		const { projectId } = this.props.match.params;
		this.props.fetchDetails(projectId);
	}

	render() {
		const { title, duration, submissions } = this.props;
		return (
			<Box bg="background" style={{ minHeight: "100vh" }}>
				<Box p={3} mx="auto" width={[1, 2 / 3, null, 2 / 5]}>
					<Heading p={1} color="text1">
						{title}
					</Heading>
					{title && <Text p={1}>{duration.toFixed(2)} sec</Text>}
					{submissions.map(submission => (
						<SubmissionItem
							key={submission.id}
							submission={submission}
						/>
					))}
				</Box>
			</Box>
		);
	}
}

const mapStateToProps = ({ projectDetailsReducer: details }) => ({
	title: details.title,
	duration: details.duration,
	submissions: details.submissions
});
const mapDispatchToProps = dispatch => ({
	fetchDetails: projectId => dispatch(fetchDetails(projectId)),
	clearDetails: () => dispatch(clearDetails())
});
export default connect(
	mapStateToProps,
	mapDispatchToProps
)(ProjectDetails);
