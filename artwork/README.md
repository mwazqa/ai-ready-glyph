# Artwork provenance

The original app icon is a circular dot matrix, white ready check and red notification dot. Android adaptive colour and monochrome variants are included. BuildAppIcon.java regenerates these assets. The small dot alphabet and generic AI lettering are original project work, covered by MIT. No proprietary Nothing font is bundled.

The ChatGPT pixel symbol derives from the official Blossom SVG in [OpenAI's 2025 logo pack](https://cdn.openai.com/brand/OpenAI-Logos-2025.zip). ChatGptSymbol.java stores 25 x 25 coverage; ChatGptTrace.java orders drawing from the inner junctions. The raw SVG is excluded from Git. [OpenAI guidelines](https://openai.com/brand/) apply; separate approval for this adaptation is not claimed.

ClaudeFrames.java contains 12 monochrome frames from a GIF supplied during personal development, preserving its one-second cycle. The raw GIF is excluded because its upstream author and redistribution licence were not established. BuildClaudeFrames.java can convert a locally supplied input; it grants no rights to that input.

The Gemini star is procedural and identifies the Gemini source.

Provider marks and reference-derived data are excluded from MIT. Attribution or conversion to pixels does not clear their rights. [Third-party notices](../THIRD_PARTY_NOTICES.md) describe the limitations.

Local reference files artwork/claude-reference.gif and artwork/openai-blossom.svg are ignored and not needed to compile the committed source.
