import showdown from "showdown"

export const markdownToHtml = markdownText => {
    const converter = new showdown.Converter()
    return converter.makeHtml(markdownText)
}